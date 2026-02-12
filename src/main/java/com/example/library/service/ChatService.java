package com.example.library.service;

import com.example.library.model.*;
import com.example.library.repository.ChatHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final BookService bookService;
    private final LoanService loanService;
    private final UserService userService;
    private final ChatHistoryRepository chatHistoryRepository;

    // 다중 사용자
    private final Map<Long, ChatContext> chatContexts = new ConcurrentHashMap<>();

    // OpenAI 연결
    private final OpenAIService openAIService;

    private ChatContext getContext(Long userId) {
        return chatContexts.computeIfAbsent(userId, id -> new ChatContext());
    }

    public String reply(User user, String message) {

        // 1️⃣ 사용자 메시지 저장
        saveUserMessage(user, message);

        ChatContext context = getContext(user.getId());
        String normalized = message.replaceAll("\\s+", "");

        String reply;

        // 상태 기반 처리
        if (context.getState() == ChatState.WAITING_FOR_EXTEND_SELECT) {
            reply = handleExtendSelection(normalized, user, context);

        // 연장 질문
        } else if (isExtendQuestion(normalized)) {
            context.resetFallback();
            reply = handleExtendLoan(user, context);

        // 내가 빌린 책
        } else if (normalized.contains("내가빌린책") || normalized.contains("내가빌린") || normalized.contains("대여한책")) {
            context.resetFallback();
            reply = handleMyLoans(user);

        // 반납 기한
        } else if (isDueDateQuestion(normalized)) {
            context.resetFallback();
            reply = handleDueDates(user);

        // 추천
        } else if (isRecommendQuestion(normalized)) {
            context.resetFallback();
            reply = handleRecommend(user);

        // 도서 검색
        } else if (isBookSearch(normalized)) {
            context.resetFallback();
            reply = handleBookSearch(message);

        // 기타 고정 응답
        } else {
            reply = fallbackOrAI(message, user, context);
        }

        // 2️⃣ 봇 메시지 저장
        saveBotMessage(user, reply);

        return reply;
    }

    private String fallbackOrAI(String message, User user, ChatContext context) {

        if (!context.isFallbackUsed()) {
            context.markFallbackUsed();
            return """
        🤔 아직 이 질문은 이해하지 못했어요.
        조금만 더 구체적으로 말씀해 주실래요?

        💡 이렇게 물어보면 도와드릴 수 있어요:
        - 대여 연장하고 싶어
        - 반납 기한 언제야?
        - 내가 빌린 책 뭐야?
        """;
        }

        String hint = context.isFallbackUsed()
                ? "[의도 추정: 도서관 일반 질문]"
                : "";

        String ctx = buildLibraryContext(user);
        return openAIService.ask(hint + message, ctx);
    }

    private String buildLibraryContext(User user) {

        // 1. 도서관 기본 정책 (하드코딩해도 됨)
        String policy = """
        [도서관 규칙]
        - 1인당 최대 3권 대여
        - 대여 기간 14일
        - 이용 시간 09:00~18:00
        """;

        // 2. 사용자 대여 목록
        List<Loan> myLoans = loanService.getActiveLoansByUser(user);
        String myLoanText;

        if (myLoans.isEmpty()) {
            myLoanText = "[사용자 대여 현황]\n- 대여 중인 도서 없음\n";
        } else {
            String list = myLoans.stream()
                    .limit(10)
                    .map(l -> "- " + l.getBook().getTitle() + " (반납기한: " + l.getDueDate() + ")")
                    .reduce("", (a, b) -> a + b + "\n");

            myLoanText = "[사용자 대여 현황]\n" + list;
        }

        // 3. 대여 가능 도서
        List<Book> available = bookService.findAvailableTop(10);
        String availableText;

        if (available.isEmpty()) {
            availableText = "[대여 가능 도서]\n- 현재 대여 가능한 도서 없음\n";
        } else {
            String list = available.stream()
                    .map(b -> "- " + b.getTitle())
                    .reduce("", (a, b) -> a + b + "\n");

            availableText = "[대여 가능 도서]\n" + list;
        }

        return policy + "\n" + myLoanText + "\n" + availableText;
    }

    // 추천 의도 판별
    private boolean isRecommendQuestion(String message) {
        return message.contains("추천")
                || message.contains("골라")
                || message.contains("아무")
                || message.contains("뭐 읽");
    }

    // 추천 로직 (AI -> DB)
    private String handleRecommend(User user) {

        List<Book> available = bookService.findAvailableTop(5);

        if (available.isEmpty()) {
            return "📭 현재 추천할 수 있는 도서가 없습니다.";
        }

        // DB 컨텍스트 생성
        String context = available.stream()
                .map(b -> "- " + b.getTitle())
                .reduce("대여 가능한 도서 목록:\n", (a, b) -> a + b + "\n");

        // AI에게 넘길 프롬프트
        String prompt = """
        너는 도서관 사서다.
        아래 [대여 가능 도서 목록] 중에서
        현재 이용자에게 가장 적합한 책 한 권을 추천해라.
        이유는 한 문장으로 설명해라.
      
        %s
        """.formatted(context);

        String ctx = buildLibraryContext(user);
        return openAIService.ask(prompt, ctx);
    }

    // 내가 빌린 책 처리
    private String handleMyLoans(User user) {

        List<Loan> loans = loanService.getActiveLoansByUser(user);

        if (loans.isEmpty()) {
            return "📭 현재 대여 중인 도서가 없습니다.";
        }

        String list = loans.stream()
                .map(l -> "• " + l.getBook().getTitle())
                .collect(Collectors.joining("\n"));

        return "📚 현재 대여 중인 도서입니다:\n" + list;
    }

    // 반납 질문 판별
    private boolean isDueDateQuestion(String message) {
        return message.contains("반납")
                || message.contains("기한")
                || message.contains("언제")
                || message.contains("마감")
                || message.contains("돌려");
    }

    // 반납 처리
    private String handleDueDates(User user) {

        List<Loan> loans = loanService.getActiveLoansByUser(user);

        if (loans.isEmpty()) {
            return "📭 현재 대여 중인 도서가 없습니다.";
        }

        StringBuilder sb = new StringBuilder("📅 반납 기한 안내입니다:\n");
        LocalDate today = LocalDate.now();

        for (Loan loan : loans) {
            LocalDate dueDate = loan.getDueDate();
            long daysLeft = ChronoUnit.DAYS.between(today, dueDate);

            sb.append("• ")
                    .append(loan.getBook().getTitle())
                    .append(" : ")
                    .append(dueDate);

            if (daysLeft >= 0) {
                sb.append(" (")
                        .append(daysLeft)
                        .append("일 남음)");
            } else {
                sb.append(" (⚠️ 연체 ")
                        .append(-daysLeft)
                        .append("일)");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    // 연장 질문 판별
    private boolean isExtendQuestion(String message) {
        return message.contains("연장")
                || message.contains("기간 늘")
                || message.contains("더 빌")
                || message.contains("대여 연장")
                || message.contains("대출 연장");
    }

    // 연장 처리
    private String handleExtendLoan(User user, ChatContext context) {

        List<Loan> loans = loanService.getActiveLoansByUser(user);

        if (loans.isEmpty()) {
            return "📭 현재 대여 중인 도서가 없어 연장할 수 없습니다.";
        }

        // 한 권 -> 바로 연장
        if (loans.size() == 1) {
            Loan loan = loans.get(0);

            String validation = validateExtendable(loan);
            if (validation != null) {
                return validation;
            }

            loanService.extendLoan(loan.getBook().getId());
            return "✅ '" + loan.getBook().getTitle() + "' 도서의 대여 기간이 14일 연장되었습니다!";
        }

        // 여러 권 -> 선택 상태
        context.setState(ChatState.WAITING_FOR_EXTEND_SELECT);
        context.setPendingLoans(loans);

        StringBuilder sb = new StringBuilder("📚 연장할 도서를 선택해주세요:\n");

        for (int i = 0; i < loans.size(); i++) {
            sb.append(i + 1)
                    .append("️⃣ ")
                    .append(loans.get(i).getBook().getTitle())
                    .append("\n");
        }

        sb.append("\n번호를 입력해주세요!");
        return sb.toString();
    }

    // 번호 입력 처리 전용
    private String handleExtendSelection(String message, User user, ChatContext context) {

        List<Loan> loans = context.getPendingLoans();

        // 안전 장치
        if (loans == null || loans.isEmpty()) {
            context.reset();
            return "⚠️ 연장할 도서 정보가 사라졌어요. 다시 연장 요청을 해주세요.";
        }

        // 숫자 입력 검증
        int index;
        try {
            index = Integer.parseInt(message) - 1;
        } catch (NumberFormatException e) {
            return "❗ 번호로 입력해주세요. (예: 1)";
        }

        if (index < 0 || index >= loans.size()) {
            return "❗ 올바른 번호를 입력해주세요.";
        }

        Loan selectedLoan = loans.get(index);

        String validation = validateExtendable(selectedLoan);
        if (validation != null) {
            context.reset();
            return validation;
        }

        loanService.extendLoan(selectedLoan.getBook().getId());
        context.reset();
        return "✅ '" + selectedLoan.getBook().getTitle() + "' 도서의 대여 기간이 14일 연장되었습니다!";
    }

    private String validateExtendable(Loan loan) {
        if (loan.isReturned()) {
            return "⚠️ 이미 반납한 도서는 연장할 수 없습니다.";
        }

        if (loan.isOverdue()) {
            return """
        ⛔ 현재 연체 중인 도서가 있어 대출 연장은 불가능합니다.
        📅 먼저 도서를 반납해 주세요.
        """;
        }

        if (!loan.canExtend()) {
            return "⚠️ 이미 연장한 도서는 다시 연장할 수 없습니다.";
        }

        return null; // 연장 가능
    }

    // 도서 검색 질문 판단
    private boolean isBookSearch(String message) {
        return message.contains("있어")
                || message.contains("있니")
                || message.contains("찾아")
                || message.contains("빌릴 수")
                || message.contains("없어?");
    }

    // 제목 추출
    private String extractTitle(String message) {
        return message
                .replaceAll("\\s+", " ")
                .replaceAll(
                        "(빌릴 수 있어|빌릴 수 있니|대여 가능|있어\\?|있니\\?|찾아줘|찾아봐|있어|있니|대여|\\?)",
                        ""
                )
                .trim();
    }

    // 실제 도서 검색 처리
    private String handleBookSearch(String message) {
        String title = extractTitle(message);

        Optional<Book> bookOpt = bookService.findByTitleContains(title);

        if (bookOpt.isEmpty()) {
            return "❌ '" + title + "' 도서를 찾을 수 없어요.";
        }

        Book book = bookOpt.get();

        if (book.isAvailable()) {
            return "📘 '" + book.getTitle() + "'은 현재 대여 가능합니다.\nQR 코드를 스캔해 대여해 주세요!";
        } else {
            return "⛔ '" + book.getTitle() + "'은 현재 대여 중입니다.";
        }
    }

    public void saveUserMessage(User user, String message) {
        chatHistoryRepository.save(
                ChatHistory.of(user, ChatRole.USER, message)
        );
    }

    public void saveBotMessage(User user, String message) {
        chatHistoryRepository.save(
                ChatHistory.of(user, ChatRole.BOT, message)
        );
    }

    public List<ChatHistory> getHistory(User user) {
        return chatHistoryRepository.findByUserOrderByCreatedAtAsc(user);
    }

    public void clearHistory(User user) {
        chatHistoryRepository.deleteByUser(user);
    }
}
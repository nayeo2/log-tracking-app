package ac.su.kdt.loggingcontrol.controller;

import ac.su.kdt.loggingcontrol.domain.CartForm;
import ac.su.kdt.loggingcontrol.domain.OrderForm;
import ac.su.kdt.loggingcontrol.logger.CustomLogger;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@RestController
public class LogController {
    Random random = new Random();

    private String computeHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/products")
    public String showProducts(
        @RequestParam(name="user-id", required = false) String userId,
        HttpServletRequest request
    ) {
        List<Integer> productIds = IntStream
            .range(0, 5)
            .mapToObj(i -> random.nextInt(100) + 1)
            .toList();

        for (int productId : productIds) {
            // 해싱 연산 추가
            String hash = computeHash("product" + productId);

            CustomLogger.logRequest(
                "l",
                "/products/",
                "GET",
                userId != null ? userId : "-",
                "-",
                String.valueOf(productId),
                hash,  // 해싱 값 추가
                "-",
                "-",
                request
            );
        }
        return "상품 리스트(5행) 조회 로그 기록됨";
    }

    @GetMapping("/products/{productId}")
    public String getProduct(
        @PathVariable String productId,
        @RequestParam(name="user-id", required = false) String userId,
        HttpServletRequest request
    ) {
        // 해싱 연산 추가
        String hash = computeHash("product" + productId);

        CustomLogger.logRequest(
            "v",
            "/products/" + productId,
            "GET",
            userId != null ? userId : "-",
            "-",
            productId,
            hash,  // 해싱 값 추가
            "-",
            "-",
            request
        );
        return "상품 조회 로그 1행 기록됨";
    }

    @PostMapping("/cart")
    public String addToCart(
        @RequestParam(name = "tr-id") String transactionId,
        @RequestBody CartForm cartForm,
        HttpServletRequest request
    ) {
        // 해싱 연산 추가
        String hash = computeHash(cartForm.getProductId().toString() + cartForm.getQuantity().toString());

        CustomLogger.logRequest(
            "c",
            "/cart",
            "POST",
            cartForm.getUserId().toString(),
            transactionId,
            cartForm.getProductId().toString(),
            cartForm.getId().toString(),
            hash,  // 해싱 값 추가
            cartForm.getQuantity().toString(),
            request
        );
        return "장바구니 추가 로그 1행 기록됨";
    }

    @PostMapping("/order")
    public String placeOrder(
        @RequestParam(name = "tr-id") String transactionId,
        @RequestBody OrderForm orderForm,
        HttpServletRequest request
    ) {
        // 해싱 연산 추가
        String hash = computeHash(orderForm.getProductId().toString() + orderForm.getQuantity().toString());

        CustomLogger.logRequest(
            "o",
            "/order",
            "POST",
            orderForm.getUserId().toString(),
            transactionId,
            orderForm.getProductId().toString(),
            orderForm.getCartId() != null ? orderForm.getCartId().toString() : "-",
            orderForm.getId().toString(),
            hash,  // 해싱 값 추가
            request
        );
        return "주문 로그 1행 기록됨";
    }
}

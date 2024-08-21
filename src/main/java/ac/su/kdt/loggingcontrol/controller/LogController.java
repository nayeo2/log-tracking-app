package ac.su.kdt.loggingcontrol.controller;

import ac.su.kdt.loggingcontrol.domain.CartForm;
import ac.su.kdt.loggingcontrol.domain.OrderForm;
import ac.su.kdt.loggingcontrol.logger.CustomLogger;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@RestController
public class LogController {
    Random random = new Random();

    // 다중 해시 연산 메서드
    private String computeMultipleHashes(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());

            // 첫 번째 해시 반복
            for (int i = 0; i < 100; i++) {
                hash = digest.digest(hash);
            }

            // 두 번째 해시 생성 및 결합
            byte[] secondHash = digest.digest(hash);
            for (int i = 0; i < 100; i++) {
                secondHash = digest.digest(secondHash);
            }

            // 두 해시를 결합하여 최종 해시 생성
            byte[] combinedHash = new byte[hash.length + secondHash.length];
            System.arraycopy(hash, 0, combinedHash, 0, hash.length);
            System.arraycopy(secondHash, 0, combinedHash, hash.length, secondHash.length);

            // 해시를 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : combinedHash) {
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
            String hash = computeMultipleHashes("product" + productId);

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
        String hash = computeMultipleHashes("product" + productId);

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
        String hash = computeMultipleHashes(cartForm.getProductId().toString() + cartForm.getQuantity().toString());

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
        String hash = computeMultipleHashes(orderForm.getProductId().toString() + orderForm.getQuantity().toString());

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

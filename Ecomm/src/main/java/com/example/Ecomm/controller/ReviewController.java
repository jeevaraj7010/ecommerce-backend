package com.example.Ecomm.controller;

import com.example.Ecomm.entity.Review;
import com.example.Ecomm.repository.OrderRepository;
import com.example.Ecomm.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private OrderRepository orderRepo;

    // ➕ Add review with Verified Buyer check
    @PostMapping
    public ResponseEntity<String> addReview(@RequestBody Review review) {

        // ✅ Rating validation
        if (review.getRating() < 1 || review.getRating() > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
        }

        // ✅ Verified buyer check: Order must exist and status must be DELIVERED
        boolean isVerifiedBuyer = orderRepo.existsByUsernameAndProductIdAndStatus(
                review.getUsername(), review.getProductId(), "DELIVERED");

        if (!isVerifiedBuyer) {
            return ResponseEntity.badRequest().body("Only verified buyers with a delivered order can review this product ❌");
        }

        // ✅ One review per user per product
        boolean exists = reviewRepo.existsByUsernameAndProductId(
                review.getUsername(), review.getProductId());

        if (exists) {
            return ResponseEntity.badRequest().body("You already reviewed this product");
        }

        reviewRepo.save(review);
        return ResponseEntity.ok("Review added successfully ✅");
    }

    // 📄 Get all reviews for a product (supports optional sorting: newest, highest, lowest)
    @GetMapping("/{productId}")
    public Object getReviews(@PathVariable Long productId,
                             @RequestParam(required = false) String sort,
                             @RequestParam(required = false) Integer page,
                             @RequestParam(required = false) Integer size) {

        List<Review> reviews = reviewRepo.findByProductId(productId);

        if ("highest".equalsIgnoreCase(sort)) {
            reviews.sort(Comparator.comparingInt(Review::getRating).reversed().thenComparing(Review::getId, Comparator.reverseOrder()));
        } else if ("lowest".equalsIgnoreCase(sort)) {
            reviews.sort(Comparator.comparingInt(Review::getRating).thenComparing(Review::getId, Comparator.reverseOrder()));
        } else {
            // default newest
            reviews.sort(Comparator.comparing(Review::getId).reversed());
        }

        if (page != null) {
            int pageSize = (size != null) ? size : 5;
            int start = page * pageSize;
            if (start >= reviews.size()) return Collections.emptyList();
            int end = Math.min(start + pageSize, reviews.size());
            return reviews.subList(start, end);
        }

        return reviews;
    }

    // ⭐ Get rating breakdown distribution (5★ to 1★ counts and percentages)
    @GetMapping("/distribution/{productId}")
    public Map<String, Object> getDistribution(@PathVariable Long productId) {
        List<Review> reviews = reviewRepo.findByProductId(productId);
        Map<Integer, Long> counts = reviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));

        Map<String, Object> result = new HashMap<>();
        long total = reviews.size();
        result.put("total", total);

        Map<String, Long> stars = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            stars.put(i + "star", counts.getOrDefault(i, 0L));
        }
        result.put("stars", stars);
        return result;
    }

    // 🔍 Check if user is a verified buyer for a product
    @GetMapping("/verified/{productId}/{username}")
    public Map<String, Boolean> checkVerifiedBuyer(@PathVariable Long productId, @PathVariable String username) {
        boolean verified = orderRepo.existsByUsernameAndProductIdAndStatus(username, productId, "DELIVERED");
        Map<String, Boolean> response = new HashMap<>();
        response.put("verified", verified);
        return response;
    }

    // ⭐ Get average rating
    @GetMapping("/average/{productId}")
    public double getAverage(@PathVariable Long productId) {

        List<Review> reviews = reviewRepo.findByProductId(productId);

        if (reviews.isEmpty()) return 0;

        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);
    }

    // ✏️ Edit review
    @PutMapping("/{id}")
    public Review updateReview(@PathVariable Long id, @RequestBody Review updatedReview) {

        Review review = reviewRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // ✅ Validate rating
        if (updatedReview.getRating() < 1 || updatedReview.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        review.setRating(updatedReview.getRating());
        review.setComment(updatedReview.getComment());

        return reviewRepo.save(review);
    }

    // ❌ Delete review
    @DeleteMapping("/{id}")
    public String deleteReview(@PathVariable Long id) {

        Review review = reviewRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        reviewRepo.delete(review);

        return "Review deleted successfully";
    }

    @GetMapping("/average/all")
    public Map<Long, Double> getAllRatings() {
        Map<Long, Double> result = new HashMap<>();

        List<Review> reviews = reviewRepo.findAll();

        Map<Long, List<Review>> grouped =
                reviews.stream().collect(Collectors.groupingBy(Review::getProductId));

        for (Long productId : grouped.keySet()) {
            double avg = grouped.get(productId)
                    .stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0);

            result.put(productId, avg);
        }

        return result;
    }
}
package com.example.Ecomm.controller;

import com.example.Ecomm.entity.Review;
import com.example.Ecomm.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepo;

    // ➕ Add review
    @PostMapping
    public String addReview(@RequestBody Review review) {

        // ✅ Rating validation
        if (review.getRating() < 1 || review.getRating() > 5) {
            return "Rating must be between 1 and 5";
        }

        // ✅ One review per user per product
        boolean exists = reviewRepo.existsByUsernameAndProductId(
                review.getUsername(), review.getProductId());

        if (exists) {
            return "You already reviewed this product";
        }

        reviewRepo.save(review);
        return "Review added successfully";
    }

    // 📄 Get all reviews for a product
    @GetMapping("/{productId}")
    public List<Review> getReviews(@PathVariable Long productId) {
        return reviewRepo.findByProductId(productId);
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
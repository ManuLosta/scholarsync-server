package com.scholarsync.server.dtos;

import com.scholarsync.server.entities.Rating;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RatingDTO {
  private String id;
  private double rating;
  private String userId;

  public static RatingDTO ratingToDTO(Rating rating) {
    RatingDTO ratingDTO = new RatingDTO();
    ratingDTO.setId(rating.getId());
    ratingDTO.setRating(rating.getRating());
    ratingDTO.setUserId(rating.getUserId().getId());
    return ratingDTO;
  }
}

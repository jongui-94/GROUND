package com.ground.domain.user.dto;

import com.ground.domain.user.entity.User;
import com.ground.domain.user.entity.UserCategory;
import lombok.*;

import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
public class UserProfileDto {

    private int follow;
    private User user;
    private int userFollowerCount;
    private int userFollowingCount;
    private List<UserCategoryDto> userCategories;
    private List<UserBoardDto> userBoardDtos;
    private List<GroundBoardDto> groundDates;
    private List<GroundCategoryDto> groundCategory;
}


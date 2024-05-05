package com.sergio.jwt.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewCourseDto {
    private String title;
    private String description;
    private Long[] authors;
    private Long[] sections;
}

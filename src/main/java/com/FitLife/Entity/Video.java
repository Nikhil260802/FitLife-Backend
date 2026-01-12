package com.FitLife.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "videos",
        indexes = {@Index(columnList = "videoName"), @Index(columnList = "videoPath")})
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int videoId;

    @NotBlank(message = "Video name is required")
    @Size(max = 255)
    private String videoName;

    @NotBlank
    private String videoPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnoreProperties("videos")
    private Course course;
}

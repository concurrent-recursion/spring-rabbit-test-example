package com.example.junitstuff.model;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Thing {
    private UUID id;
    private String name;
    private ZonedDateTime created = ZonedDateTime.now();
}

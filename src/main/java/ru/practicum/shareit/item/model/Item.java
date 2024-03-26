package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(length = 512, nullable = false)
    private String description;
    @EqualsAndHashCode.Exclude
    @Column(nullable = false)
    private Boolean available;
    @Column(name = "user_id", nullable = false)
    private Long userId;
}
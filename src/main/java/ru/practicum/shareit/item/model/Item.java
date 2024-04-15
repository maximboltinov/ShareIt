package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.itemRequest.model.ItemRequest;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Item {
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
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest itemRequest;
}
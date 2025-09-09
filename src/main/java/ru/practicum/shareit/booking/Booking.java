package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.base.BaseEntity;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "booking", schema = "public")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Booking implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalDateTime start;
    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalDateTime end;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;
    @NotNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}

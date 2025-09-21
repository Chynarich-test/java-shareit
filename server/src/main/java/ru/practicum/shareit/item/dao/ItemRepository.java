package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByOwnerId(long ownerId, Pageable pageable);

    @Query("""
            select it
            from Item as it
            where (lower(it.name) like lower(concat('%', ?1, '%'))
                   or lower(it.description) like lower(concat('%', ?1, '%'))
                   ) and it.available = true
            """)
    Page<Item> searchByText(String text, Pageable pageable);

    List<Item> findByRequestId(long request);
}

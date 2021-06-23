package ru.khaksbyt.repository.memory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.khaksbyt.model.memory.History;

import java.util.UUID;

@Repository
public interface HistoryRepository extends JpaRepository<History, UUID> {

}

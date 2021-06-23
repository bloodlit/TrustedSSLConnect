package ru.khaksbyt.repository.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.khaksbyt.model.primary.NsiItemInfoDTO;

@Repository
public interface NsiItemInfoRepository extends JpaRepository<NsiItemInfoDTO, Long> {

}

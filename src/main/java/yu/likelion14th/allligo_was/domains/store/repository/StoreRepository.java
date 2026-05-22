package yu.likelion14th.allligo_was.domains.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yu.likelion14th.allligo_was.domains.store.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
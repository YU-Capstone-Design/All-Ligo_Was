package yu.likelion14th.allligo_was.domains.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yu.likelion14th.allligo_was.domains.auth.entity.Verification;

import java.util.Optional;

public interface VerificationRepository extends JpaRepository<Verification, Long> {

    Optional<Verification> findByEmail(String email);

    Optional<Verification> findByEmailAndToken(String email, String token);
}
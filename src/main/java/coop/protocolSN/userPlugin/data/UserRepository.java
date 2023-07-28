package coop.protocolSN.userPlugin.data;

import coop.protocolSN.userPlugin.data.entity.NodeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<NodeUser, UUID> {
}

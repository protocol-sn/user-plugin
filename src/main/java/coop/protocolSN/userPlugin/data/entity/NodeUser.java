package coop.protocolSN.userPlugin.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "NodeUser")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeUser {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID userId;
    private String username;
    private String email;
    private boolean verified;
}

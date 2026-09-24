package com.winnerx0.pika.taxact;

import com.winnerx0.pika.audit.AuditMetadata;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "tax_acts")
@Getter
@Setter
@ToString
public class TaxAct extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, columnDefinition = "VECTOR(1536)")
    @JdbcTypeCode(SqlTypes.VECTOR)
    private float[] embedding;
}

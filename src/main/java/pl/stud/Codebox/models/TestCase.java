package pl.stud.Codebox.models;


import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Builder
@Table(name = "test_cases",
uniqueConstraints = @UniqueConstraint(columnNames = {"problem_id", "position"}))
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition ="TEXT" ,nullable = false)
    private String input;

    @Column(name = "expected_output",columnDefinition = "TEXT", nullable = false)
    private String expectedOutput;

    @Column(name="is_sample", nullable = false)
    @Builder.Default
    private Boolean Sample = false;

    @Column(nullable = false)
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;
}

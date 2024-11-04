package interview.takehomeassignmentexv2.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Account {
    private Integer id;
    private BigDecimal balance;
}

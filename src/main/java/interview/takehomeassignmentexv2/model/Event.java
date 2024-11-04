package interview.takehomeassignmentexv2.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Event {
    Integer origin;
    Integer destination;
    EventType type;
    BigDecimal amount;
}

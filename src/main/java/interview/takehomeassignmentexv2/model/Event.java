package interview.takehomeassignmentexv2.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Event {
    String origin;
    String destination;
    EventType type;
    BigDecimal amount;
}

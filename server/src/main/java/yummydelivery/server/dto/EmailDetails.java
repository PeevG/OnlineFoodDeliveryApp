package yummydelivery.server.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EmailDetails {
    private String recipient;
    private String messageBody;
    private String subject;
}

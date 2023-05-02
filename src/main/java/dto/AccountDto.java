package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private String id;
    private String firstName;
    private String lastName;
    private String nationalIdNumber;
    private String accountNumber;
    private String routingNumber;
    private String bankName;
}

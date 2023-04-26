package model;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "accounts")
public class Account {

    @Id
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @NotBlank
    private String routingNumber;

    @NotBlank
    private String nationalIdNumber;

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String bankName;

    private BigDecimal balance;

    public BigDecimal getBalance() {
        return balance == null ? BigDecimal.ZERO : balance;
    }

}

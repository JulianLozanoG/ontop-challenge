package mapper;

import dto.AccountDto;
import model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    @Mapping(target = "id", ignore = true)
    Account toEntity(AccountDto accountDTO);

    AccountDto toDto(Account account);

    List<AccountDto> toDtoList(List<Account> accountList);
}
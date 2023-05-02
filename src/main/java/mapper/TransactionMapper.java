package mapper;

import dto.TransactionDto;
import model.transaction.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(componentModel = "spring", uses = AccountMapper.class)
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(target = "id", ignore = true)
    Transaction toTransaction(TransactionDto transactionDTO);

    @Mapping(target = "creationDate", source = "creationDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    TransactionDto toDto(Transaction transaction);

    List<TransactionDto> toDtoList(List<Transaction> transactionList);

    Transaction toEntity(TransactionDto transactionDTO);
}

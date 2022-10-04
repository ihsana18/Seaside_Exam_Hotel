package indocyber.exam.service;

import indocyber.exam.dto.customer.CustomerGridDTO;
import indocyber.exam.dto.customer.UpdateCustomerDTO;
import indocyber.exam.dto.transaction.TransactionGridDTO;
import indocyber.exam.entity.Customer;
import indocyber.exam.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {

    List<Customer> findAll();

    void deleteTransaction(Transaction transaction);

    Customer findByUsername(String username);

    void deleteById(Integer id);

    List<CustomerGridDTO> getCustomerGrid(Integer page, String name);

    Page<CustomerGridDTO> findAllDTO(Pageable pageable, String name);

    Page<TransactionGridDTO> getCustomerTransaction(Pageable pageable, String username);

    long getTotalPages(String name);

    UpdateCustomerDTO findCustomerToUpdate(String username);

    void updateCustomer(UpdateCustomerDTO dto);

    void updateCustomerFromDTO(UpdateCustomerDTO dto);

    Boolean checkExistingEmail(String username, String email);

}

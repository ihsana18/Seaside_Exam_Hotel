package indocyber.exam.service;

import indocyber.exam.configuration.MvcSecurityConfig;
import indocyber.exam.dto.customer.CustomerGridDTO;
import indocyber.exam.dto.customer.UpdateCustomerDTO;
import indocyber.exam.dto.transaction.TransactionGridDTO;
import indocyber.exam.entity.Account;
import indocyber.exam.entity.Customer;
import indocyber.exam.entity.Transaction;
import indocyber.exam.repository.AccountRepository;
import indocyber.exam.repository.CustomerRepository;
import indocyber.exam.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private final int rowsInPage = 10;

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public void deleteTransaction(Transaction transaction) {
        transactionRepository.delete(transaction);
    }

    @Override
    public Customer findByUsername(String username) {
        CustomerGridDTO dto = customerRepository.findByUsername(username);
        return customerRepository.findById(dto.getId()).get();
    }

    @Override
    public void deleteById(Integer id) {
        customerRepository.deleteById(id);
    }

    @Override
    public List<CustomerGridDTO> getCustomerGrid(Integer page, String name) {
        Pageable pagination = PageRequest.of(page -1,rowsInPage, Sort.by("name"));

        List<CustomerGridDTO> grid = customerRepository.findAll(name, pagination);

        return grid;
    }

    @Override
    public Page<CustomerGridDTO> findAllDTO(Pageable pageable, String name) {
        return customerRepository.findAllCustomerPage(name, pageable);
    }

    @Override
    public Page<TransactionGridDTO> getCustomerTransaction(Pageable pageable, String username) {
        return transactionRepository.findAllByUsernamePage(username, pageable);
    }

    @Override
    public long getTotalPages(String name) {
        double totalData = (double)(customerRepository.count(name));
        long totalPage = (long)(Math.ceil(totalData / rowsInPage));
        return totalPage;
    }

    @Override
    public UpdateCustomerDTO findCustomerToUpdate(String username) {
        CustomerGridDTO cust = customerRepository.findByUsername(username);
        UpdateCustomerDTO dto = new UpdateCustomerDTO(
                username,
                cust.getName(),
                cust.getEmail(),
                cust.getAddress()
        );
        return dto;
    }

    @Override
    public void updateCustomer(UpdateCustomerDTO dto) {
        PasswordEncoder passwordEncoder = MvcSecurityConfig.passwordEncoder();
        String hashPassword = passwordEncoder.encode(dto.getPassword());

        Customer oldCustomer = customerRepository.findById(dto.getId()).get();

        Account account = new Account(
                dto.getUsername(),
                hashPassword,
                "Customer"
        );

        Customer customer = new Customer(
                dto.getName(),
                dto.getEmail(),
                dto.getAddress(),
                dto.getUsername()
        );

        customer.setListTransaction(oldCustomer.getListTransaction());

        accountRepository.save(account);
        customerRepository.save(customer);
    }

    @Override
    public void updateCustomerFromDTO(UpdateCustomerDTO dto) {
        PasswordEncoder passwordEncoder = MvcSecurityConfig.passwordEncoder();
        String hashPassword = passwordEncoder.encode(dto.getPassword());

        CustomerGridDTO custDto = customerRepository.findByUsername(dto.getUsername());

        Customer oldCustomer = customerRepository.findById(custDto.getId()).get();

        Account account = new Account(
                dto.getUsername(),
                hashPassword,
                "Customer"
        );

        Customer customer = new Customer(
                dto.getName(),
                dto.getEmail(),
                dto.getAddress(),
                dto.getUsername()
        );

        customer.setListTransaction(oldCustomer.getListTransaction());

        accountRepository.save(account);
        customerRepository.save(customer);
    }

    @Override
    public Boolean checkExistingEmail(String username, String email) {
        Long totalEmail = customerRepository.countExistingEmail(username, email);
        return totalEmail > 0;
    }
}

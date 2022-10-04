package indocyber.exam.service;


import indocyber.exam.dto.customer.InsertCustomerDTO;

public interface AccountService {

    String getAccountRole(String username);

    void registerCustomer(InsertCustomerDTO dto);

    Boolean checkExistingUsername(String username);
}

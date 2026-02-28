package com.cinema.deposit;

import java.util.List;

public interface DepositDao {
    
    /**
     * For Users: Submit a new deposit request with transaction details
     */
    void submitDeposit(Deposit deposit);

    /**
     * For Users: Retrieve personal deposit history
     */
    List<Deposit> findByUserId(int userId);
    
    /**
     * For Admins: Retrieve all pending requests that need approval
     */
    List<Deposit> findAllPending();

    /**
     * For Admins: Retrieve the entire history of all deposits (Approved/Rejected/Pending)
     */
    List<Deposit> findAll();

    /**
     * For Admins: Approve or Reject a deposit and update user balance
     * @param depositId The ID of the deposit
     * @param status 'approved' or 'rejected'
     * @param adminNote Reason for rejection or confirmation note
     */
    void processDeposit(int depositId, String status, String adminNote);
}
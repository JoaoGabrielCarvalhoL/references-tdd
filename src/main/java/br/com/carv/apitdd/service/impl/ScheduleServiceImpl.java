package br.com.carv.apitdd.service.impl;

import br.com.carv.apitdd.model.Loan;
import br.com.carv.apitdd.service.EmailService;
import br.com.carv.apitdd.service.LoanService;
import br.com.carv.apitdd.service.ScheduleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

    private final LoanService loanService;

    private final EmailService emailService;

    @Value("${application.mail.lateloans.message}")
    private String message;

    public ScheduleServiceImpl(LoanService loanService, EmailService emailService) {
        this.loanService = loanService;
        this.emailService = emailService;
    }

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendEmailToLateLoans() {
        List<Loan> allLateLoans = loanService.getAllLateLoans();
        List<String> emails =  allLateLoans.stream().map(loan -> loan.getCustomerEmail())
                .collect(Collectors.toList());

        emailService.sendMails(this.message, emails);
    }
}

package com.shopMe.demo.service;

import com.shopMe.demo.dto.Request.RequestDto;
import com.shopMe.demo.dto.wallet.WalletDto;
import com.shopMe.demo.model.User;
import com.shopMe.demo.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.shopMe.demo.repository.WalletRepository;
import com.shopMe.demo.repository.UserRepository;

import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RequestService requestService;

    @Autowired
    LogsService logsService;

    private UserService userService;

    @Autowired
    public WalletService(@Lazy UserService userService) {
        this.userService = userService;
    }


    public WalletDto getUserWalletDto(User user) {
        Optional<Wallet> OWallet = walletRepository.findByUser(user);
        return new WalletDto(OWallet.get().getId(),OWallet.get().getSTA(),OWallet.get().getMoney());
    }

    public Wallet getUserWallet(User user) {
        Optional<Wallet> OWallet = walletRepository.findByUser(user);
        return OWallet.get();
    }

    public void createWallet(User user) {
        Wallet wallet  = new Wallet(0,0,user);
         walletRepository.save(wallet);
    }

    public void updateWallet(User user,WalletDto walletDto) {
        Optional<Wallet> OWallet  = walletRepository.findByUser(user);
        Wallet wallet = OWallet.get();

        wallet.setSTA(walletDto.getSTA());
        wallet.setMoney(walletDto.getMoney());
        walletRepository.save(wallet);
    }

    public void depositWallet(double money, User user) {
        Optional<Wallet> OWallet = walletRepository.findByUser(user);

        OWallet.get().setMoney(OWallet.get().getMoney()+money);
        walletRepository.save(OWallet.get());

    }

    public void requestWithdraw(double money, User user) {
        Optional<Wallet> OWallet = walletRepository.findByUser(user);
        double walletMoney;
        walletMoney = OWallet.get().getMoney()-money;
        OWallet.get().setPendingMoney(money);
        OWallet.get().setMoney(walletMoney);
        walletRepository.save(OWallet.get());

        RequestDto requestDto = new RequestDto();
        requestDto.setStatus("pending");
        requestDto.setMoney(money);
        requestDto.setMessage("withdraw");
        requestService.addRequest(user,requestDto);
    }

    public void payout(double money, User user) {
        Optional<Wallet> OWallet = walletRepository.findByUser(user);
        OWallet.get().setPendingMoney(0);
        //more will do here to send user real money to bank account
        walletRepository.save(OWallet.get());
    }

    public void exchangeMoney(double sta, User user) {
        Optional<Wallet> OWallet = walletRepository.findByUser(user);
        Optional<Wallet> ownerWallet = walletRepository.findByUser(userService.getOwner());
        double moneyLeft, walletSta;
        walletSta = OWallet.get().getSTA()+sta;
        ownerWallet.get().setSTA(ownerWallet.get().getSTA()-sta);
        moneyLeft = OWallet.get().getMoney()-(sta*10000);
        OWallet.get().setMoney(moneyLeft);
        OWallet.get().setSTA(walletSta);

        walletRepository.save(ownerWallet.get());
        walletRepository.save(OWallet.get());
        String message = "Exchanged money";
        logsService.addExchangeMoneyToStaLog(user,message,sta,"success");
    }

    public void sendSta(double sta, User user, String receiver) {
        double senderSta,receiverSta;
        Optional<Wallet> OWallet = walletRepository.findByUser(user);
        User receiverAccount = userRepository.findByEmail(receiver);
        Optional<Wallet> OWallet2 = walletRepository.findByUser(receiverAccount);



        senderSta = OWallet.get().getSTA()-sta;
        receiverSta =  OWallet2.get().getSTA()+sta;

        OWallet.get().setSTA(senderSta);
        OWallet2.get().setSTA(receiverSta);
        walletRepository.save(OWallet.get());
        walletRepository.save(OWallet2.get());

        String message = "sending";
        logsService.StaSendingLog(user,OWallet2.get().getUser().getId(),message,sta,"success");
    }
}

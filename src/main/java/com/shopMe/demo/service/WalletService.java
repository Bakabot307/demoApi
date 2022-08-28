package com.shopMe.demo.service;

import com.shopMe.demo.dto.wallet.WalletDto;
import com.shopMe.demo.model.User;
import com.shopMe.demo.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
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
    LogsService logsService;


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

        OWallet.get().setMoney(money);
        walletRepository.save(OWallet.get());

    }

    public void exchangeMoney(double sta, User user) {
        Optional<Wallet> OWallet = walletRepository.findByUser(user);
        double moneyLeft;


        moneyLeft = OWallet.get().getMoney()-(sta*10000);
        OWallet.get().setMoney(moneyLeft);
        OWallet.get().setSTA(sta);
        walletRepository.save(OWallet.get());
        String message = "Exchanged money";
        logsService.addLogToUserWithSta(user,message,sta,"success");
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

        String message = "sent sta";
        logsService.StaSendingLog(user,OWallet2.get().getUser().getId(),message,sta,"success");
    }
}

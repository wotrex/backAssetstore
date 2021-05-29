package Code.assetstore.controllers;

import Code.assetstore.models.Assets;
import Code.assetstore.models.User;
import Code.assetstore.payloads.MessageResponse;
import Code.assetstore.repository.UserRepository;
import Code.assetstore.security.services.AssetService;
import Code.assetstore.security.services.UserDetailsImpl;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/pay")
public class PaymentController {
    @Value("${SK_TEST_KEY}")
    String key;
    @Autowired
    private AssetService assetService;
    @Autowired
    private UserRepository userRepository;
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/session/{assetId}")
    public MessageResponse createІSession(@PathVariable(name = "assetId") String assetId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
            Assets assets = assetService.getAsset(assetId);
            Stripe.apiKey = key;
            Price price = Price.create(PriceCreateParams.builder().setCurrency("uah").
                    setProductData(PriceCreateParams.ProductData.builder().setName(assets.getName()).build())
                    .setUnitAmount(assets.getCost())
                    .build());
            SessionCreateParams.LineItem item1 = SessionCreateParams.LineItem.builder().setPrice(price.getId()).setQuantity(1L).build();
            SessionCreateParams param = SessionCreateParams.builder().
                    setCancelUrl("http://assetstore.herokuapp.com/assets").
                    setMode(SessionCreateParams.Mode.PAYMENT).
                    setSuccessUrl("http://assetstore.herokuapp.com/successPayment").
                    addLineItem(item1).
                    setClientReferenceId(user.get().getId()).
                    putMetadata("assetId", assetId).
                    addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD).
                    build();
            Session session = Session.create(param);
            User newUser = user.get();
            List<String> ses = new ArrayList<>();
            if(newUser.getSessions() != null){
                ses = newUser.getSessions();
            }
            ses.add(session.getId());
            newUser.setSessions(ses);
            userRepository.save(newUser);
            TimerTask timerTask = new TimerTask() {

                @Override
                public void run() {
                    PaymentIntent pay = null;
                    try {
                        pay = PaymentIntent.retrieve(session.getPaymentIntent());
                    } catch (StripeException e) {
                        e.printStackTrace();
                    }
                    assert pay != null;
                    if(pay.getStatus().equals("requires_payment_method")){
                        try {
                            PaymentIntent paycancel = pay.cancel();
                        } catch (StripeException e) {
                            e.printStackTrace();
                        }
                        for(int i = 0; i < newUser.getSessions().size(); i++){
                            if(newUser.getSessions().get(i).equals(session.getId())){
                                List<String> nesList;
                                nesList = newUser.getSessions();
                                nesList.remove(i);
                                User user = newUser;
                                user.setSessions(nesList);
                                userRepository.save(user);
                                break;
                            }
                        }
                    }
                }
            };
            Timer timer = new Timer("SessionOverTime");//create a new Timer
            timer.schedule(timerTask, 60000);
            return new MessageResponse(session.getId());
        } catch (StripeException e) {
            e.printStackTrace();
            return new MessageResponse("fail");

        }
    }
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/success")
    public void successSession() throws StripeException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        User newUser = user.get();
        Stripe.apiKey = key;
        if (newUser.getSessions() != null) {
            for (int i = 0; i < newUser.getSessions().size(); i++) {
                Session session = Session.retrieve(newUser.getSessions().get(i));
                if (session.getPaymentStatus().equals("paid")) {
                    List<String> items = new ArrayList<>();
                    if (newUser.getItems() != null) {
                        items = newUser.getItems();
                        if(!newUser.getItems().contains(session.getMetadata().get("assetId"))) {
                            items.add(session.getMetadata().get("assetId"));
                        }
                    }
                    newUser.setItems(items);
                    List<String> ses = newUser.getSessions();
                    ses.remove(i);
                    userRepository.save(newUser);
                }
            }
        }
    }

}

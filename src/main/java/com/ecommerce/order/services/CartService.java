package com.ecommerce.order.services;

import com.ecommerce.order.clients.ProductServiceClient;
import com.ecommerce.order.clients.UserServiceClient;
import com.ecommerce.order.dtos.CartItemRequest;
import com.ecommerce.order.dtos.ProductResponse;
import com.ecommerce.order.dtos.UserResponse;
import com.ecommerce.order.models.Cartitem;
import com.ecommerce.order.repositoris.CartitemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CartitemRepository cartitemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;

//    private final UserRepository userRepository;
//    private final ProductRepository productRepository;

    public boolean addToCart(String userId, CartItemRequest request) {
       ProductResponse productResponse = productServiceClient.getProductDetails(String.valueOf(request.getProductId()));
          if (productResponse == null)
           return false;

       if (productResponse.getStockQuantity() < request.getQuantity())
           return false;

       UserResponse userResponse =  userServiceClient.getUserDetails(userId);

       if (userResponse == null)
           return false;
//
//       Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
//       if (userOpt.isEmpty())
//           return false;
//       User user = userOpt.get();

        Cartitem existingCartItem = cartitemRepository.findByUserIdAndProductId(userId, String.valueOf(request.getProductId()));

        if(existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartitemRepository.save(existingCartItem);
        }else{
            Cartitem cartitem = new Cartitem();
            cartitem.setUserId(userId);
            cartitem.setProductId(String.valueOf(request.getProductId()));
            cartitem.setQuantity(request.getQuantity());
            cartitem.setQuantity(request.getQuantity());
            cartitem.setPrice(BigDecimal.valueOf(1000.00));
            cartitemRepository.save(cartitem);
        }

    return true;

    }

    public boolean deleteItemFromCart(String userId, String productId) {
//        Optional<Product> productOpt = productRepository.findById(productId);
//        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        Cartitem cartItem = cartitemRepository.findByUserIdAndProductId(userId, productId);

        if (cartItem != null) {

            cartitemRepository.delete(cartItem);
            return true;
        }
        return false;
    }

    public List<Cartitem> getCart(String userId) {
        return cartitemRepository.findByUserId(userId);
    }

    public void clearCart(String userId) {
        cartitemRepository.deleteByUserId(userId);

    }
}

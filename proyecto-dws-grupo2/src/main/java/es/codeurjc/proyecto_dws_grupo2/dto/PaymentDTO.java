package es.codeurjc.proyecto_dws_grupo2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PaymentDTO {

    @NotBlank(message = "El titular de la tarjeta es obligatorio")
    private String cardHolder;

    @Pattern(regexp = "^\\d{16}$", message = "La tarjeta debe tener exactamente 16 dígitos")
    private String cardNumber;

    @Pattern(regexp = "^(0[1-9]|1[0-2])\\/\\d{2}$", message = "El formato debe ser MM/AA")
    private String expiry;

    @Pattern(regexp = "^\\d{3}$", message = "El CVV debe tener 3 dígitos")
    private String cvv;

    
    public String getCardHolder() { return cardHolder; }
    public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getExpiry() { return expiry; }
    public void setExpiry(String expiry) { this.expiry = expiry; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
}
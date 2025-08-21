package com.cl.msipen03.exceptions;

public class RemittanceNotFoundException extends InternalMSIPEN03Exception {
    public RemittanceNotFoundException(String remittanceOrderId) {
        super("Remittance operation not found for ID: " + remittanceOrderId);
    }
}

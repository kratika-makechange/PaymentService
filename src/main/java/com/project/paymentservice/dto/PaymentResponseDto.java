package com.project.paymentservice.dto;

    public class PaymentResponseDto {
        private String paymentId;
        private String status;
        private String message;

        public PaymentResponseDto(String paymentId, String status, String message) {
            this.paymentId = paymentId;
            this.status = status;
            this.message = message;
        }

        public String getPaymentId() {
            return paymentId;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "PaymentResponseDto{" +
                    "paymentId='" + paymentId + '\'' +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

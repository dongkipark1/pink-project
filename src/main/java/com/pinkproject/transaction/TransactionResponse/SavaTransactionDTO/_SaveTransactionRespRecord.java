package com.pinkproject.transaction.TransactionResponse.SavaTransactionDTO;

import com.pinkproject.transaction.enums.CategoryIn;
import com.pinkproject.transaction.enums.CategoryOut;
import com.pinkproject.transaction.enums.TransactionType;

public record _SaveTransactionRespRecord(
        TransactionType transactionType,
        String yearMonthDate,
        String time,
        String amount,
        CategoryIn categoryIn,
        CategoryOut categoryOut,
        String assets,
        String description
) {
}

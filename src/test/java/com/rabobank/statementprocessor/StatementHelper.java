package com.rabobank.statementprocessor;

import com.rabobank.statementprocessor.validation.dto.Statement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StatementHelper {

    public static List<Statement> getValid(int numberOfValid) {
        return IntStream.range(1, numberOfValid + 1).mapToObj(StatementHelper::validStatement).collect(Collectors.toList());
    }

    public static Statement validStatement(int reference) {
        BigDecimal startBalance = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble() * 100).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal mutation = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble() * 100).setScale(2, RoundingMode.HALF_EVEN);
        boolean operation = ThreadLocalRandom.current().nextBoolean();
        if (!operation)
            mutation = mutation.multiply(BigDecimal.valueOf(-1)).setScale(2, RoundingMode.HALF_EVEN);

        BigDecimal endBalance = startBalance.add(mutation);

        return Statement.builder().reference((long) reference).account("NL").description("any").startBalance(startBalance)
                .mutation(mutation).endBalance(endBalance).build();
    }
}

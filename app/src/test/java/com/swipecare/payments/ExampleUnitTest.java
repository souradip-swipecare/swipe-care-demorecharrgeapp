package com.swipecare.payments;

import static com.swipecare.payments.utils.CheckOnBoardingStatusKt.isUserOnboarded;

import org.junit.Test;

import static org.junit.Assert.*;

import com.swipecare.payments.model.UserOnboardingDetailsResponse;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void subtraction_wrong() {
        assertEquals(5, 9 - 4);
    }

    @Test
    public void isUserOnboardedTest() {
        UserOnboardingDetailsResponse userOnboardingDetailsResponse = new UserOnboardingDetailsResponse("TXN", "User Onboarded", null);
        assertTrue(isUserOnboarded(userOnboardingDetailsResponse));
    }
}
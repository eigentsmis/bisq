/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.desktop.components.paymentmethods;

import bisq.desktop.components.InputTextField;
import bisq.desktop.util.FormBuilder;
import bisq.desktop.util.Layout;
import bisq.desktop.util.validation.PopmoneyValidator;

import bisq.core.locale.Res;
import bisq.core.locale.TradeCurrency;
import bisq.core.payment.AccountAgeWitnessService;
import bisq.core.payment.PaymentAccount;
import bisq.core.payment.PopmoneyAccount;
import bisq.core.payment.payload.PaymentAccountPayload;
import bisq.core.payment.payload.PopmoneyAccountPayload;
import bisq.core.util.BSFormatter;
import bisq.core.util.validation.InputValidator;

import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import static bisq.desktop.util.FormBuilder.addTopLabelTextField;
import static bisq.desktop.util.FormBuilder.addTopLabelTextFieldWithCopyIcon;

public class PopmoneyForm extends PaymentMethodForm {
    private final PopmoneyAccount account;
    private final PopmoneyValidator validator;
    private InputTextField accountIdInputTextField;

    public static int addFormForBuyer(GridPane gridPane, int gridRow, PaymentAccountPayload paymentAccountPayload) {
        FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("payment.account.owner"),
                ((PopmoneyAccountPayload) paymentAccountPayload).getHolderName());
        addTopLabelTextFieldWithCopyIcon(gridPane, ++gridRow, Res.get("payment.popmoney.accountId"), ((PopmoneyAccountPayload) paymentAccountPayload).getAccountId());
        return gridRow;
    }

    public PopmoneyForm(PaymentAccount paymentAccount, AccountAgeWitnessService accountAgeWitnessService, PopmoneyValidator popmoneyValidator, InputValidator inputValidator, GridPane gridPane, int gridRow, BSFormatter formatter) {
        super(paymentAccount, accountAgeWitnessService, inputValidator, gridPane, gridRow, formatter);
        this.account = (PopmoneyAccount) paymentAccount;
        this.validator = popmoneyValidator;
    }

    @Override
    public void addFormForAddAccount() {
        gridRowFrom = gridRow + 1;

        InputTextField holderNameInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow,
                Res.get("payment.account.owner"));
        holderNameInputTextField.setValidator(inputValidator);
        holderNameInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            account.setHolderName(newValue);
            updateFromInputs();
        });

        accountIdInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow, Res.get("payment.popmoney.accountId"));
        accountIdInputTextField.setValidator(validator);
        accountIdInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            account.setAccountId(newValue);
            updateFromInputs();
        });

        final TradeCurrency singleTradeCurrency = account.getSingleTradeCurrency();
        final String nameAndCode = singleTradeCurrency != null ? singleTradeCurrency.getNameAndCode() : "";
        FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"), nameAndCode);
        addLimitations();
        addAccountNameTextFieldWithAutoFillToggleButton();
    }

    @Override
    protected void autoFillNameTextField() {
        setAccountNameWithString(accountIdInputTextField.getText());
    }

    @Override
    public void addFormForDisplayAccount() {
        gridRowFrom = gridRow;
        addTopLabelTextField(gridPane, gridRow, Res.get("payment.account.name"), account.getAccountName(), Layout.FIRST_ROW_AND_GROUP_DISTANCE);
        FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("shared.paymentMethod"), Res.get(account.getPaymentMethod().getId()));
        FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("payment.account.owner"),
                account.getHolderName());
        TextField field = FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("payment.popmoney.accountId"), account.getAccountId()).second;
        field.setMouseTransparent(false);
        final TradeCurrency singleTradeCurrency = account.getSingleTradeCurrency();
        final String nameAndCode = singleTradeCurrency != null ? singleTradeCurrency.getNameAndCode() : "";
        FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"), nameAndCode);
        addLimitations();
    }

    @Override
    public void updateAllInputsValid() {
        allInputsValid.set(isAccountNameValid()
                && inputValidator.validate(account.getHolderName()).isValid
                && validator.validate(account.getAccountId()).isValid
                && account.getTradeCurrencies().size() > 0);
    }
}
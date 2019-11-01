package com.certusnet.fabric.chaincode.bankmaster.chaincode;

import com.certusnet.fabric.chaincode.bankmaster.domain.*;
import com.certusnet.fabric.chaincode.common.util.DateTimeUtils;
import com.certusnet.fabric.chaincode.common.util.JsonUtils;
import com.certusnet.fabric.chaincode.common.util.MD5Utils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * BankMaster应用的智能合约
 * <p>
 * !!!注意：请不要在在链码中使用与本机相关的信息(例如本机当前时间)，以免可能造成背书结果不一致而导致事物失败，请在SDK客户端使用transient data进行传递!!!
 *
 * @author pengpeng
 * @date 2018年12月25日 下午4:09:58
 */
public class BankMasterChaincode extends ChaincodeBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(BankMasterChaincode.class);

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final Double DEFAULT_ACCOUNT_BALANCE = 0.0;

    private static final String KEY_BANK_BALANCE = "BANK_BALANCE";

    private static final String KEY_PREFIX_CUSTOMER_ACCOUNT = "CUSTOMER_ACCOUNT_";

    private static final String KEY_PREFIX_ACCOUNT_TRANSACTION = "ACCOUNT_TRANSACTION_";

    private static final String KEY_PREFIX_ACCOUNT = "ACCOUNT_";

    private static final String KEY_PREFIX_BUYER = "BUYER_";

    private static final String KEY_PREFIX_SELLER = "SELLER_";

    private static final String KEY_PREFIX_ELECTRICITYTRADINGRECORD = "ELECTRICITYTRADINGRECORD_";

    private static final String KEY_PREFIX_ACCOUNT_HISTORY = "ACCOUNT_HISTORY_";

    private static final String KEY_PREFIX_ENCRYPTED_OFFER = "ENCRYPTED_OFFER_";

    private static final String KEY_PREFIX_QUALIFIED_OFFER = "QUALIFIED_OFFER_";

    private static final String KEY_PREFIX_AUTHENTIC_OFFER = "AUTHENTIC_OFFER_";

    private static final String KEY_PREFIX_ENCRYPTED_OFFER_LIST = "ENCRYPTED_OFFER_LIST_";

    private static final String KEY_PREFIX_QUALIFIED_OFFER_LIST = "QUALIFIED_OFFER_LIST_";

    private static final String KEY_PREFIX_AUTHENTIC_OFFER_LIST = "AUTHENTIC_OFFER_LIST_";

    private static final String FLAG_TRUE = "TRUE";

    private static final String FLAG_FALSE = "FALSE";

    /**
     * 智能合约初始化
     * 参数列表：parameters[0] = 100		<银行资产金额>
     */
    @Override
    public Response init(ChaincodeStub stub) {
        List<String> parameters = stub.getParameters();
        LOGGER.info(">>> parameters = {}, args = {}", stub.getParameters(), stub.getStringArgs());
        Double bankBalance = DEFAULT_ACCOUNT_BALANCE;
        if (parameters.size() == 1 && NumberUtils.isCreatable(StringUtils.trimToEmpty(parameters.get(0)))
                && (bankBalance = Double.parseDouble(StringUtils.trimToEmpty(parameters.get(0)))) > 0) {
            stub.putStringState(KEY_BANK_BALANCE, bankBalance.toString()); //初始化银行资产
            return newSuccessResponse("初始化智能合约成功!");
        } else {
            return newErrorResponse("初始化智能合约失败：参数只能有一个，并且为非负数值类型数据!");
        }
    }

    /**
     * 调用智能合约
     */
    @Override
    public Response invoke(ChaincodeStub stub) {
        String function = stub.getFunction();
        List<String> args = stub.getParameters();
        LOGGER.info(">>> 调用智能合约开始，function = {}, args = {}", function, args);
        Response response = null;
        try {
            response = doInvoke(stub, function, args);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            response = newErrorResponse(String.format("调用智能合约出错：%s", ExceptionUtils.getRootCauseMessage(e)));
        }
        LOGGER.info("<<< 调用智能合约结束，response = [status = {}, message = {}, payload = {}]", response.getStatus().getCode(), response.getMessage(), response.getPayload() == null ? null : new String(response.getPayload(), CHARSET));
        return response;
    }

    protected Response doInvoke(ChaincodeStub stub, String function, List<String> args) throws Exception {
        if ("createAccount".equals(function)) {
            return createAccount(stub, args);
        } else if ("depositMoney".equals(function)) {
            return depositMoney(stub, args);
        } else if ("drawalMoney".equals(function)) {
            return drawalMoney(stub, args);
        } else if ("transferAccount".equals(function)) {
            return transferAccount(stub, args);
        } else if ("getAccountBalance".equals(function)) {
            return getAccountBalance(stub, args);
        } else if ("getAllAccounts".equals(function)) {
            return getAllAccounts(stub, args);
        } else if ("getAccountTransactionRecords".equals(function)) {
            return getAccountTransactionRecords(stub, args);
        } else if ("initAccount".equals(function)) {
            return initAccount(stub, args);
        } else if ("addElectricityTradingRecord".equals(function)) {
            return addElectricityTradingRecord(stub, args);
        } else if ("addMoney".equals(function)) {
            return addMoney(stub, args);
        } else if ("reduceMoney".equals(function)) {
            return reduceMoney(stub, args);
        } else if ("transferAccounts".equals(function)) {
            return transferAccounts(stub, args);
        } else if ("queryAccount".equals(function)) {
            return queryAccount(stub, args);
        } else if ("getAccountHistory".equals(function)) {
            return getAccountHistory(stub, args);
        } else if ("getElectricityTradingRecordHistory".equals(function)) {
            return getElectricityTradingRecordHistory(stub, args);
        } else if ("addEncryptedOffer".equals(function)) {
            return addEncryptedOffer(stub, args);
        } else if ("addAuthenticOffer".equals(function)) {
            return addAuthenticOffer(stub, args);
        } else if ("verificationOffer".equals(function)) {
            return verificationOffer(stub, args);
        } else if ("getQualifiedOfferList".equals(function)) {
            return getQualifiedOfferList(stub, args);
        }
        return newErrorResponse(String.format("不存在的智能合约方法名: %s", function));
    }

    /**
     * 客户开户
     * 参数列表：parameters[0] = {"accountNo":null,"realName":"彭三","idCardNo":"342425198607284712","mobilePhone":"15151887280"} 		<客户资料json>
     *
     * @param stub
     * @param args
     * @return
     * @throws Exception
     */
    protected Response createAccount(ChaincodeStub stub, List<String> args) throws Exception {
        String requestBody = null;
        if (args.size() == 1 && JsonUtils.isJsonObject((requestBody = args.get(0)))) {
            CustomerAccount account = JsonUtils.json2Object(requestBody, CustomerAccount.class);
            if (StringUtils.isBlank(account.getRealName())) {
                return newErrorResponse("请求参数不合法：开户人真实姓名不能为空!");
            }
            if (StringUtils.isBlank(account.getIdCardNo())) {
                return newErrorResponse("请求参数不合法：开户人身份证号码不能为空!");
            }
            if (StringUtils.isBlank(account.getMobilePhone())) {
                return newErrorResponse("请求参数不合法：开户人手机号码不能为空!");
            }
            account.setAccountBalance(ObjectUtils.defaultIfNull(account.getAccountBalance(), DEFAULT_ACCOUNT_BALANCE));

            String jsonAccount = saveCustomerAccount(stub, account); //保存账户

            AccountTransaction transaction = new AccountTransaction(stub.getTxId(), account.getAccountNo(), 0.0,
                    account.getAccountBalance(), account.getAccountBalance(), null,
                    AccountTransactionType.CREATE_ACCOUNT.name(), AccountTransactionType.CREATE_ACCOUNT.getDescription(), account.getCreatedTime());

            saveAccountTransaction(stub, transaction); //保存账户交易快照

            saveBankBalance(stub, account.getAccountBalance()); //保存银行余额

            return newSuccessResponse("开户成功!", jsonAccount.getBytes(CHARSET));
        } else {
            return newErrorResponse("请求参数不合法：参数只能有一个，并且为json类型数据!");
        }
    }

    protected Response initAccount(ChaincodeStub stub, List<String> args) throws Exception {
        String requestBody = null;
        if (args.size() == 1 && JsonUtils.isJsonObject((requestBody = args.get(0)))) {
            Account account = JsonUtils.json2Object(requestBody, Account.class);
            if (StringUtils.isBlank(account.getRealName())) {
                return newErrorResponse("请求参数不合法：开户人真实姓名不能为空!");
            }
            account.setAccountBalance(ObjectUtils.defaultIfNull(account.getAccountBalance(), DEFAULT_ACCOUNT_BALANCE));

            String jsonAccount = saveAccount(stub, account); //保存账户

            AccountHistory transaction = new AccountHistory(stub.getTxId(), account.getAccountNo(), 0.0,
                    account.getAccountBalance(), account.getAccountBalance(), null,
                    AccountHistoryType.CREATE_ACCOUNT.name(), AccountHistoryType.CREATE_ACCOUNT.getDescription(), account.getCreatedTime());

            saveAccountHistory(stub, transaction); //保存账户交易快照
            return newSuccessResponse("开户成功!", jsonAccount.getBytes(CHARSET));
        } else {
            return newErrorResponse("请求参数不合法：参数只能有一个，并且为json类型数据!");
        }
    }

    protected Response addElectricityTradingRecord(ChaincodeStub stub, List<String> args) throws Exception {
        String requestBody = null;
        if (args.size() == 1 && JsonUtils.isJsonObject((requestBody = args.get(0)))) {
            ElectricityTradingRecord electricityTradingRecord = JsonUtils.json2Object(requestBody, ElectricityTradingRecord.class);
            String jsonElectricityTradingRecord = saveElectricityTradingRecord(stub, electricityTradingRecord); //保存账户交易快照
            return newSuccessResponse("开户成功!", jsonElectricityTradingRecord.getBytes(CHARSET));
        } else {
            return newErrorResponse("请求参数不合法：参数只能有一个，并且为json类型数据!");
        }
    }

    /**
     * 客户存款
     * 参数列表：parameters[0] = 6225778834761431			<客户账户卡号>
     * parameters[1] = 500						<存款金额>
     *
     * @param stub
     * @param args
     * @return
     * @throws Exception
     */
    protected synchronized Response depositMoney(ChaincodeStub stub, List<String> args) throws Exception {
        Map<String, byte[]> transients = stub.getTransient();
        transients.forEach((key, value) -> {
            System.out.println(key + ":" + value);
        });
        String accountNo = null;
        String amountValue = null;
        if (args.size() == 2) {
            accountNo = StringUtils.trimToEmpty(args.get(0));
            if (!accountNo.matches("\\d{16}")) {
                return newErrorResponse("请求参数不合法：第一个参数为账户卡号，必须是16位银行卡号!");
            }
            amountValue = StringUtils.trimToEmpty(args.get(1));
            if (!NumberUtils.isCreatable(amountValue)) {
                return newErrorResponse("请求参数不合法：第二个参数为存款金额，必须是大于0的数值类型!");
            }
            Double amount = Double.valueOf(amountValue);
            if (amount <= 0) {
                return newErrorResponse("请求参数不合法：第二个参数为存款金额，必须是大于0的数值类型!");
            }

            CustomerAccount account = getCustomerAccountByNo(stub, accountNo);
            if (account == null) {
                return newErrorResponse(String.format("对不起，账号(%s)不存在!", accountNo));
            }

            byte[] transactionTimeBytes = transients.get("transactionTime");
            if (ArrayUtils.isEmpty(transactionTimeBytes)) {
                return newErrorResponse("请求参数不合法：交易时间(transients[transactionTime])不能为空!");
            }
            String transactionTime = new String(transactionTimeBytes, CHARSET);

            double balance = account.getAccountBalance();
            account.setAccountBalance(balance + amount); //更新余额

            saveCustomerAccount(stub, account); //保存账户

            AccountTransaction transaction = new AccountTransaction(stub.getTxId(), account.getAccountNo(), balance,
                    account.getAccountBalance(), amount, null,
                    AccountTransactionType.DEPOSITE_MONEY.name(), AccountTransactionType.DEPOSITE_MONEY.getDescription(), transactionTime);

            saveAccountTransaction(stub, transaction); //保存账户交易快照

            saveBankBalance(stub, amount); //保存银行余额

            return newSuccessResponse("存款成功!", account.getAccountBalance().toString().getBytes(CHARSET));
        } else {
            return newErrorResponse("请求参数不合法：参数只能有两个!");
        }
    }

    protected synchronized Response addMoney(ChaincodeStub stub, List<String> args) throws Exception {
        Map<String, byte[]> transients = stub.getTransient();
        transients.forEach((key, value) -> {
            System.out.println(key + ":" + value);
        });
        String accountNo = null;
        String amountValue = null;
        if (args.size() == 2) {
            accountNo = StringUtils.trimToEmpty(args.get(0));
            amountValue = StringUtils.trimToEmpty(args.get(1));
            if (!NumberUtils.isCreatable(amountValue)) {
                return newErrorResponse("请求参数不合法：第二个参数为存款金额，必须是大于0的数值类型!");
            }
            Double amount = Double.valueOf(amountValue);
            if (amount <= 0) {
                return newErrorResponse("请求参数不合法：第二个参数为存款金额，必须是大于0的数值类型!");
            }

            Account account = getAccountByNo(stub, accountNo);
            if (account == null) {
                return newErrorResponse(String.format("对不起，账号(%s)不存在!", accountNo));
            }

            byte[] transactionTimeBytes = transients.get("transactionTime");
            if (ArrayUtils.isEmpty(transactionTimeBytes)) {
                return newErrorResponse("请求参数不合法：交易时间(transients[transactionTime])不能为空!");
            }
            String transactionTime = new String(transactionTimeBytes, CHARSET);

            double balance = account.getAccountBalance();
            account.setAccountBalance(balance + amount); //更新余额

            saveAccount(stub, account); //保存账户

            AccountHistory transaction = new AccountHistory(stub.getTxId(), account.getAccountNo(), balance,
                    account.getAccountBalance(), amount, null,
                    AccountHistoryType.DEPOSITE_MONEY.name(), AccountHistoryType.DEPOSITE_MONEY.getDescription(), transactionTime);

            saveAccountHistory(stub, transaction); //保存账户交易快照
            return newSuccessResponse("存款成功!", account.getAccountBalance().toString().getBytes(CHARSET));
        } else {
            return newErrorResponse("请求参数不合法：参数只能有两个!");
        }
    }

    /**
     * 客户取款
     * 参数列表：parameters[0] = 6225778834761431			<客户账户卡号>
     * parameters[1] = 500						<取款金额>
     *
     * @param stub
     * @param args
     * @return
     * @throws Exception
     */
    protected synchronized Response drawalMoney(ChaincodeStub stub, List<String> args) throws Exception {
        Map<String, byte[]> transients = stub.getTransient();
        transients.forEach((key, value) -> {
            System.out.println(key + ":" + value);
        });
        String accountNo = null;
        String amountValue = null;
        if (args.size() == 2) {
            accountNo = StringUtils.trimToEmpty(args.get(0));
            if (!accountNo.matches("\\d{16}")) {
                return newErrorResponse("请求参数不合法：第一个参数为账户卡号，必须是16位银行卡号!");
            }
            amountValue = StringUtils.trimToEmpty(args.get(1));
            if (!NumberUtils.isCreatable(amountValue)) {
                return newErrorResponse("请求参数不合法：第二个参数为取款金额，必须是大于0的数值类型!");
            }
            Double amount = Double.valueOf(amountValue);
            if (amount <= 0) {
                return newErrorResponse("请求参数不合法：第二个参数为取款金额，必须是大于0的数值类型!");
            }

            CustomerAccount account = getCustomerAccountByNo(stub, accountNo);
            if (account == null) {
                return newErrorResponse(String.format("对不起，账号(%s)不存在!", accountNo));
            }

            byte[] transactionTimeBytes = transients.get("transactionTime");
            if (ArrayUtils.isEmpty(transactionTimeBytes)) {
                return newErrorResponse("请求参数不合法：交易时间(transients[transactionTime])不能为空!");
            }
            String transactionTime = new String(transactionTimeBytes, CHARSET);

            double balance = account.getAccountBalance();
            account.setAccountBalance(balance - amount); //更新余额

            saveCustomerAccount(stub, account); //保存账户

            AccountTransaction transaction = new AccountTransaction(stub.getTxId(), account.getAccountNo(), balance,
                    account.getAccountBalance(), amount, null,
                    AccountTransactionType.DRAWAL_MONEY.name(), AccountTransactionType.DRAWAL_MONEY.getDescription(), transactionTime);

            saveAccountTransaction(stub, transaction); //保存账户交易快照

            saveBankBalance(stub, -amount); //保存银行余额

            return newSuccessResponse("取款成功!", account.getAccountBalance().toString().getBytes(CHARSET));
        } else {
            return newErrorResponse("请求参数不合法：参数只能有两个!");
        }
    }

    protected synchronized Response reduceMoney(ChaincodeStub stub, List<String> args) throws Exception {
        Map<String, byte[]> transients = stub.getTransient();
        transients.forEach((key, value) -> {
            System.out.println(key + ":" + value);
        });
        String accountNo = null;
        String amountValue = null;
        if (args.size() == 2) {
            accountNo = StringUtils.trimToEmpty(args.get(0));
            amountValue = StringUtils.trimToEmpty(args.get(1));
            if (!NumberUtils.isCreatable(amountValue)) {
                return newErrorResponse("请求参数不合法：第二个参数为取款金额，必须是大于0的数值类型!");
            }
            Double amount = Double.valueOf(amountValue);
            if (amount <= 0) {
                return newErrorResponse("请求参数不合法：第二个参数为取款金额，必须是大于0的数值类型!");
            }

            Account account = getAccountByNo(stub, accountNo);
            if (account == null) {
                return newErrorResponse(String.format("对不起，账号(%s)不存在!", accountNo));
            }

            byte[] transactionTimeBytes = transients.get("transactionTime");
            if (ArrayUtils.isEmpty(transactionTimeBytes)) {
                return newErrorResponse("请求参数不合法：交易时间(transients[transactionTime])不能为空!");
            }
            String transactionTime = new String(transactionTimeBytes, CHARSET);

            double balance = account.getAccountBalance();
            account.setAccountBalance(balance - amount); //更新余额

            saveAccount(stub, account); //保存账户

            AccountHistory transaction = new AccountHistory(stub.getTxId(), account.getAccountNo(), balance,
                    account.getAccountBalance(), amount, null,
                    AccountHistoryType.DRAWAL_MONEY.name(), AccountHistoryType.DRAWAL_MONEY.getDescription(), transactionTime);

            saveAccountHistory(stub, transaction); //保存账户交易快照

            return newSuccessResponse("取款成功!", account.getAccountBalance().toString().getBytes(CHARSET));
        } else {
            return newErrorResponse("请求参数不合法：参数只能有两个!");
        }
    }

    /**
     * 客户转账
     * 参数列表：parameters[0] = 6225778834761431			<转出账户卡号>
     * parameters[1] = 6225778834761432			<转入账户卡号>
     * parameters[2] = 500						<转账金额>
     *
     * @param stub
     * @param args
     * @return
     * @throws Exception
     */
    protected synchronized Response transferAccount(ChaincodeStub stub, List<String> args) throws Exception {
        Map<String, byte[]> transients = stub.getTransient();
        transients.forEach((key, value) -> {
            System.out.println(key + ":" + value);
        });
        String accountANo = null, accountBNo = null;
        String amountValue = null;
        if (args.size() == 3) {
            accountANo = StringUtils.trimToEmpty(args.get(0));
            if (!accountANo.matches("\\d{16}")) {
                return newErrorResponse("请求参数不合法：第一个参数为转出账户卡号，必须是16位银行卡号!");
            }
            accountBNo = StringUtils.trimToEmpty(args.get(1));
            if (!accountBNo.matches("\\d{16}")) {
                return newErrorResponse("请求参数不合法：第二个参数为转入账户卡号，必须是16位银行卡号!");
            }
            amountValue = StringUtils.trimToEmpty(args.get(2));
            if (!NumberUtils.isCreatable(amountValue)) {
                return newErrorResponse("请求参数不合法：第三个参数为转账金额，必须是大于0的数值类型!");
            }
            Double amount = Double.valueOf(amountValue);
            if (amount <= 0) {
                return newErrorResponse("请求参数不合法：第三个参数为转账金额，必须是大于0的数值类型!");
            }

            CustomerAccount accountA = getCustomerAccountByNo(stub, accountANo);
            if (accountA == null) {
                return newErrorResponse(String.format("对不起，转出账号(%s)不存在!", accountANo));
            }
            CustomerAccount accountB = getCustomerAccountByNo(stub, accountBNo);
            if (accountB == null) {
                return newErrorResponse(String.format("对不起，转入账号(%s)不存在!", accountBNo));
            }

            byte[] transactionTimeBytes = transients.get("transactionTime");
            if (ArrayUtils.isEmpty(transactionTimeBytes)) {
                return newErrorResponse("请求参数不合法：交易时间(transients[transactionTime])不能为空!");
            }
            String transactionTime = new String(transactionTimeBytes, CHARSET);

            double balanceA = accountA.getAccountBalance();
            accountA.setAccountBalance(balanceA - amount); //更新余额

            double balanceB = accountB.getAccountBalance();
            accountB.setAccountBalance(balanceB + amount); //更新余额

            saveCustomerAccount(stub, accountA); //保存账户

            AccountTransaction transactionA = new AccountTransaction(stub.getTxId(), accountA.getAccountNo(), balanceA,
                    accountA.getAccountBalance(), amount, accountB.getAccountNo(),
                    AccountTransactionType.TRANSFER_OUT.name(), AccountTransactionType.TRANSFER_OUT.getDescription(), transactionTime);

            saveAccountTransaction(stub, transactionA); //保存账户交易快照

            saveBankBalance(stub, -amount); //保存银行余额

            saveCustomerAccount(stub, accountB); //保存账户

            AccountTransaction transactionB = new AccountTransaction(stub.getTxId(), accountB.getAccountNo(), balanceB,
                    accountB.getAccountBalance(), amount, accountA.getAccountNo(),
                    AccountTransactionType.TRANSFER_IN.name(), AccountTransactionType.TRANSFER_IN.getDescription(), transactionTime);

            saveAccountTransaction(stub, transactionB); //保存账户交易快照

            saveBankBalance(stub, amount); //保存银行余额

            return newSuccessResponse("转账成功!", accountA.getAccountBalance().toString().getBytes(CHARSET));
        } else {
            return newErrorResponse("请求参数不合法：参数只能有三个!");
        }
    }

    protected synchronized Response transferAccounts(ChaincodeStub stub, List<String> args) throws Exception {
        Map<String, byte[]> transients = stub.getTransient();
        transients.forEach((key, value) -> {
            System.out.println(key + ":" + value);
        });
        String accountANo = null, accountBNo = null;
        String amountValue = null;
        if (args.size() == 3) {
            accountANo = StringUtils.trimToEmpty(args.get(0));
            accountBNo = StringUtils.trimToEmpty(args.get(1));
            amountValue = StringUtils.trimToEmpty(args.get(2));
            if (!NumberUtils.isCreatable(amountValue)) {
                return newErrorResponse("请求参数不合法：第三个参数为转账金额，必须是大于0的数值类型!");
            }
            Double amount = Double.valueOf(amountValue);
            if (amount <= 0) {
                return newErrorResponse("请求参数不合法：第三个参数为转账金额，必须是大于0的数值类型!");
            }

            Account accountA = getAccountByNo(stub, accountANo);
            if (accountA == null) {
                return newErrorResponse(String.format("对不起，转出账号(%s)不存在!", accountANo));
            }
            Account accountB = getAccountByNo(stub, accountBNo);
            if (accountB == null) {
                return newErrorResponse(String.format("对不起，转入账号(%s)不存在!", accountBNo));
            }

            byte[] transactionTimeBytes = transients.get("transactionTime");
            if (ArrayUtils.isEmpty(transactionTimeBytes)) {
                return newErrorResponse("请求参数不合法：交易时间(transients[transactionTime])不能为空!");
            }
            String transactionTime = new String(transactionTimeBytes, CHARSET);

            double balanceA = accountA.getAccountBalance();
            accountA.setAccountBalance(balanceA - amount); //更新余额

            double balanceB = accountB.getAccountBalance();
            accountB.setAccountBalance(balanceB + amount); //更新余额

            saveAccount(stub, accountA); //保存账户

            AccountHistory transactionA = new AccountHistory(stub.getTxId(), accountA.getAccountNo(), balanceA,
                    accountA.getAccountBalance(), amount, accountB.getAccountNo(),
                    AccountHistoryType.TRANSFER_OUT.name(), AccountHistoryType.TRANSFER_OUT.getDescription(), transactionTime);

            saveAccountHistory(stub, transactionA); //保存账户交易快照


            saveAccount(stub, accountB); //保存账户

            AccountHistory transactionB = new AccountHistory(stub.getTxId(), accountB.getAccountNo(), balanceB,
                    accountB.getAccountBalance(), amount, accountA.getAccountNo(),
                    AccountHistoryType.TRANSFER_IN.name(), AccountHistoryType.TRANSFER_IN.getDescription(), transactionTime);

            saveAccountHistory(stub, transactionB); //保存账户交易快照

            return newSuccessResponse("转账成功!", accountA.getAccountBalance().toString().getBytes(CHARSET));
        } else {
            return newErrorResponse("请求参数不合法：参数只能有三个!");
        }
    }

    /**
     * 查询账户余额
     * 参数列表：parameters[0] = 6225778834761431			<账户卡号>
     *
     * @param stub
     * @param args
     * @return
     * @throws Exception
     */
    protected Response getAccountBalance(ChaincodeStub stub, List<String> args) throws Exception {
        String accountNo = null;
        if (args.size() == 1 && (accountNo = StringUtils.trimToEmpty(args.get(0))).matches("\\d{16}")) {
            CustomerAccount account = getCustomerAccountByNo(stub, accountNo);
            if (account == null) {
                return newErrorResponse(String.format("对不起，账号(%s)不存在!", accountNo));
            }
            return newSuccessResponse("查询余额成功!", account.getAccountBalance().toString().getBytes(CHARSET));
        } else {
            return newErrorResponse("请求参数不合法：参数只能有一个，且必须是16位银行卡号!");
        }
    }

    protected Response queryAccount(ChaincodeStub stub, List<String> args) throws Exception {
        String accountNo = null;
        if (args.size() == 1) {
            accountNo = StringUtils.trimToEmpty(args.get(0));
            Account account = getAccountByNo(stub, accountNo);
            if (account == null) {
                return newErrorResponse(String.format("对不起，账号(%s)不存在!", accountNo));
            }
            return newSuccessResponse("查询余额成功!", account.getAccountBalance().toString().getBytes(CHARSET));
        } else {
            return newErrorResponse("请求参数不合法：参数只能有一个!");
        }
    }

    protected Response getAccountHistory(ChaincodeStub stub, List<String> args) throws Exception {
        String accountNo = null;
        int fetchSize = 10;
        if (CollectionUtils.isEmpty(args)) {
            return newErrorResponse("请求参数不合法：至少需要1个参数!");
        } else {
            accountNo = StringUtils.trimToEmpty(args.get(0));
        }

        if (accountNo == null) {
            return newErrorResponse("请求参数不合法：第1个参数账户不能为空!");
        } else if (args.size() > 2) {
            return newErrorResponse("请求参数不合法：参数最多只能有2个、第2个是返回记录条数!");
        } else {
            if (args.size() == 2) {
                try {
                    fetchSize = Integer.valueOf(args.get(1));
                    fetchSize = fetchSize > 0 ? fetchSize : 10;
                } catch (Exception e) {
                }
            }
            List<String> records = new ArrayList<String>();
            String key = createAccountHistoryKey(stub, accountNo);
            QueryResultsIterator<KeyModification> qrIterator = stub.getHistoryForKey(key);
            int count = 0;
            if (qrIterator != null) {
                Iterator<KeyModification> it = qrIterator.iterator();
                while (it.hasNext()) {
                    records.add(it.next().getStringValue());
                    if (++count >= fetchSize) {
                        break;
                    }
                }
            }
            return newSuccessResponse("查询账户交易记录成功!", records.toString().getBytes(CHARSET));
        }
    }

    protected Response getElectricityTradingRecordHistory(ChaincodeStub stub, List<String> args) throws Exception {
        String accountNo = null;
        int fetchSize = 10;
        if (CollectionUtils.isEmpty(args)) {
            return newErrorResponse("请求参数不合法：至少需要1个参数!");
        } else {
            accountNo = StringUtils.trimToEmpty(args.get(0));
        }

        if (accountNo == null) {
            return newErrorResponse("请求参数不合法：第1个参数账户不能为空!");
        } else if (args.size() > 2) {
            return newErrorResponse("请求参数不合法：参数最多只能有2个、第2个是返回记录条数!");
        } else {
            if (args.size() == 2) {
                try {
                    fetchSize = Integer.valueOf(args.get(1));
                    fetchSize = fetchSize > 0 ? fetchSize : 10;
                } catch (Exception e) {
                }
            }
            List<String> records = new ArrayList<String>();
            String key = createElectricityTradingRecordKey(stub, accountNo);
            QueryResultsIterator<KeyModification> qrIterator = stub.getHistoryForKey(key);
            int count = 0;
            if (qrIterator != null) {
                Iterator<KeyModification> it = qrIterator.iterator();
                while (it.hasNext()) {
                    records.add(it.next().getStringValue());
                    if (++count >= fetchSize) {
                        break;
                    }
                }
            }
            return newSuccessResponse("查询账户交易记录成功!", records.toString().getBytes(CHARSET));
        }
    }

    /**
     * 查询所有账户列表
     *
     * @param stub
     * @param args
     * @return
     * @throws Exception
     */
    protected Response getAllAccounts(ChaincodeStub stub, List<String> args) throws Exception {
        List<String> accounts = new ArrayList<String>();
        String compositeKey = stub.createCompositeKey(KEY_PREFIX_CUSTOMER_ACCOUNT).toString();
        QueryResultsIterator<KeyValue> results = stub.getStateByPartialCompositeKey(compositeKey);
        for (Iterator<KeyValue> it = results.iterator(); it.hasNext(); ) {
            KeyValue kv = it.next();
            accounts.add(kv.getStringValue());
        }
        results.close();
        String payload = "[" + StringUtils.join(accounts, ",") + "]";
        return newSuccessResponse("查询所有账户列表成功!", payload.getBytes(CHARSET));
    }

    /**
     * 查询账户的最近多少条交易记录
     * 参数列表：parameters[0] = 6225778834761431			<账户卡号>
     * parameters[1] = 10							<返回记录条数>
     *
     * @param stub
     * @param args
     * @return
     * @throws Exception
     */
    protected Response getAccountTransactionRecords(ChaincodeStub stub, List<String> args) throws Exception {
        String accountNo = null;
        int fetchSize = 10;
        if (CollectionUtils.isEmpty(args)) {
            return newErrorResponse("请求参数不合法：至少需要1个参数(16位银行卡号)!");
        } else if (!(accountNo = StringUtils.trimToEmpty(args.get(0))).matches("\\d{16}")) {
            return newErrorResponse("请求参数不合法：第1个参数必须是16位银行卡号!");
        } else if (args.size() > 2) {
            return newErrorResponse("请求参数不合法：参数最多只能有2个，且第一个是16位银行卡号、第2个是返回记录条数!");
        } else {
            if (args.size() == 2) {
                try {
                    fetchSize = Integer.valueOf(args.get(1));
                    fetchSize = fetchSize > 0 ? fetchSize : 10;
                } catch (Exception e) {
                }
            }
            List<String> records = new ArrayList<String>();
            String key = createAccountTransactionKey(stub, accountNo);
            QueryResultsIterator<KeyModification> qrIterator = stub.getHistoryForKey(key);
            int count = 0;
            if (qrIterator != null) {
                Iterator<KeyModification> it = qrIterator.iterator();
                while (it.hasNext()) {
                    records.add(it.next().getStringValue());
                    if (++count >= fetchSize) {
                        break;
                    }
                }
            }
            return newSuccessResponse("查询账户交易记录成功!", records.toString().getBytes(CHARSET));
        }
    }

    protected String createCustomerAccountKey(ChaincodeStub stub, String accountNo) {
        return stub.createCompositeKey(KEY_PREFIX_CUSTOMER_ACCOUNT, accountNo).toString();
    }

    protected String createAccountKey(ChaincodeStub stub, String accountNo) {
        return stub.createCompositeKey(KEY_PREFIX_ACCOUNT, accountNo).toString();
    }

    protected String createBuyerAccountKey(ChaincodeStub stub, String accountNo) {
        return stub.createCompositeKey(KEY_PREFIX_BUYER, accountNo).toString();
    }

    protected String createElectricityTradingRecordKey(ChaincodeStub stub, String accountNo) {
        return stub.createCompositeKey(KEY_PREFIX_ELECTRICITYTRADINGRECORD, accountNo).toString();
    }

    protected String createSellerAccountKey(ChaincodeStub stub, String accountNo) {
        return stub.createCompositeKey(KEY_PREFIX_SELLER, accountNo).toString();
    }

    protected String createAccountTransactionKey(ChaincodeStub stub, String accountNo) {
        return stub.createCompositeKey(KEY_PREFIX_ACCOUNT_TRANSACTION, accountNo).toString();
    }

    protected String createAccountHistoryKey(ChaincodeStub stub, String accountNo) {
        return stub.createCompositeKey(KEY_PREFIX_ACCOUNT_HISTORY, accountNo).toString();
    }

    protected CustomerAccount getCustomerAccountByNo(ChaincodeStub stub, String accountNo) {
        String key = createCustomerAccountKey(stub, accountNo);
        String value = stub.getStringState(key);
        if (!StringUtils.isEmpty(value)) {
            return JsonUtils.json2Object(value, CustomerAccount.class);
        }
        return null;
    }

    protected Account getAccountByNo(ChaincodeStub stub, String accountNo) {
        String key = createAccountKey(stub, accountNo);
        String value = stub.getStringState(key);
        if (!StringUtils.isEmpty(value)) {
            return JsonUtils.json2Object(value, Account.class);
        }
        return null;
    }

    protected String saveCustomerAccount(ChaincodeStub stub, CustomerAccount account) {
        String jsonAccount = JsonUtils.object2Json(account);
        stub.putStringState(createCustomerAccountKey(stub, account.getAccountNo()), jsonAccount); //修改账本
        return jsonAccount;
    }

    protected String saveAccount(ChaincodeStub stub, Account account) {
        String jsonAccount = JsonUtils.object2Json(account);
        stub.putStringState(createAccountKey(stub, account.getAccountNo()), jsonAccount); //修改账本
        return jsonAccount;
    }

    protected String saveElectricityTradingRecord(ChaincodeStub stub, ElectricityTradingRecord electricityTradingRecord) {
        String jsonElectricityTradingRecord = JsonUtils.object2Json(electricityTradingRecord);
        stub.putStringState(createElectricityTradingRecordKey(stub, electricityTradingRecord.getBuyerId()), jsonElectricityTradingRecord); //修改账本
        stub.putStringState(createElectricityTradingRecordKey(stub, electricityTradingRecord.getSellerId()), jsonElectricityTradingRecord); //修改账本
        return jsonElectricityTradingRecord;
    }

    protected String saveAccountTransaction(ChaincodeStub stub, AccountTransaction transaction) {
        String jsonTransaction = JsonUtils.object2Json(transaction);
        stub.putStringState(createAccountTransactionKey(stub, transaction.getTransactionAccountNo()), jsonTransaction); //修改账本
        return jsonTransaction;
    }

    protected String saveAccountHistory(ChaincodeStub stub, AccountHistory transaction) {
        String jsonTransaction = JsonUtils.object2Json(transaction);
        stub.putStringState(createAccountHistoryKey(stub, transaction.getTransactionAccountNo()), jsonTransaction); //修改账本
        return jsonTransaction;
    }

    protected Double saveBankBalance(ChaincodeStub stub, Double delta) {
        Double bankBalance = Double.valueOf(stub.getStringState(KEY_BANK_BALANCE));
        bankBalance = bankBalance + delta;
        stub.putStringState(KEY_BANK_BALANCE, String.valueOf(bankBalance));
        return bankBalance;
    }

    /**
     * 密封报价
     * 参数列表：parameters[0] = uuid			        <密封报价批次Id>
     * 参数列表：parameters[1] = 密封报价json			<密封报价json>
     * parameters[] = {}							<返回当前报价>
     *
     * @param stub
     * @param args
     * @return
     * @throws Exception
     */
    protected synchronized Chaincode.Response addEncryptedOffer(ChaincodeStub stub, List<String> args) throws Exception {
//        Map<String, byte[]> transients = stub.getTransient();
        String requestBody = null;
        if (args.size() == 2 && JsonUtils.isJsonObject((requestBody = args.get(1)))) {
            EncryptedOffer encryptedOffer = JsonUtils.json2Object(requestBody, EncryptedOffer.class);
            String encryptedOfferList_id = args.get(0);
            String key = createKey(stub, KEY_PREFIX_ENCRYPTED_OFFER_LIST, encryptedOfferList_id);
            EncryptedOfferList encryptedOfferList = getChainCodeObject(stub, key, EncryptedOfferList.class);
            if (encryptedOfferList != null) {
                encryptedOfferList.getEncryptedOfferList().add(encryptedOffer);
            } else {
                encryptedOfferList = new EncryptedOfferList();
                encryptedOfferList.setId(encryptedOfferList_id);
                encryptedOfferList.setTime(DateTimeUtils.formatNow());
                encryptedOfferList.setFlag(FLAG_TRUE);
                List<EncryptedOffer> list = new ArrayList<>();
                list.add(encryptedOffer);
                encryptedOfferList.setEncryptedOfferList(list);

            }
            stub.putStringState(key, JsonUtils.object2Json(encryptedOfferList));
            return newSuccessResponse("密封报价成功!", encryptedOffer.toString().getBytes(CHARSET));
        } else {
            return newErrorResponse("请求参数不合法：参数只能有二个，并且第二个参数为json类型数据!");
        }
    }

    /**
     * 真实报价
     * 参数列表：parameters[0] = uuid        			<真实报价批次Id>
     * 参数列表：parameters[1] = 真实报价json		　　<真实报价json>
     * parameters[] = {}							<返回当前报价>
     *
     * @param stub
     * @param args
     * @return
     * @throws Exception
     */
    protected synchronized Chaincode.Response addAuthenticOffer(ChaincodeStub stub, List<String> args) throws Exception {
//        Map<String, byte[]> transients = stub.getTransient();
        String requestBody = null;
        if (args.size() == 2 && JsonUtils.isJsonObject((requestBody = args.get(1)))) {
            AuthenticOffer authenticOffer = JsonUtils.json2Object(requestBody, AuthenticOffer.class);
            String authenticOfferList_id = args.get(0);
            String key = createKey(stub, KEY_PREFIX_AUTHENTIC_OFFER_LIST, authenticOfferList_id);
            AuthenticOfferList authenticOfferList = getChainCodeObject(stub, key, AuthenticOfferList.class);
            if (authenticOfferList != null) {
                authenticOfferList.getAuthenticOfferList().add(authenticOffer);
            } else {
                authenticOfferList = new AuthenticOfferList();
                authenticOfferList.setId(authenticOfferList_id);
                authenticOfferList.setTime(DateTimeUtils.formatNow());
                authenticOfferList.setFlag(FLAG_TRUE);
                List<AuthenticOffer> list = new ArrayList<>();
                list.add(authenticOffer);
                authenticOfferList.setAuthenticOfferList(list);
            }
            stub.putStringState(key, JsonUtils.object2Json(authenticOfferList));
            return newSuccessResponse("真实报价成功!", authenticOffer.toString().getBytes(CHARSET));
        } else {
            return newErrorResponse("请求参数不合法：参数只能有二个，并且第二个参数为json类型数据!");
        }
    }

    protected synchronized Chaincode.Response verificationOffer(ChaincodeStub stub, List<String> args) throws Exception {
        if (args.size() == 1) {
            String offer_id = args.get(0);
            String a_key = createKey(stub, KEY_PREFIX_AUTHENTIC_OFFER_LIST, offer_id);
            String e_key = createKey(stub, KEY_PREFIX_ENCRYPTED_OFFER_LIST, offer_id);
            String q_key = createKey(stub, KEY_PREFIX_QUALIFIED_OFFER_LIST, offer_id);
            QualifiedOfferList qualifiedOfferList = new QualifiedOfferList();
            AuthenticOfferList authenticOfferList = getChainCodeObject(stub, a_key, AuthenticOfferList.class);
            EncryptedOfferList encryptedOfferList = getChainCodeObject(stub, e_key, EncryptedOfferList.class);
            if (authenticOfferList != null && encryptedOfferList != null) {
                if (authenticOfferList.getFlag().equals(FLAG_FALSE) || encryptedOfferList.getFlag().equals(FLAG_FALSE)) {
                    return newErrorResponse("该批次报价已经验证！");
                }
                List<QualifiedOffer> list = new ArrayList<>();
                qualifiedOfferList.setId(offer_id);
                qualifiedOfferList.setTime(DateTimeUtils.formatNow());
                qualifiedOfferList.setFlag(FLAG_TRUE);
                qualifiedOfferList.setQualifiedOfferList(list);
                List<AuthenticOffer> authenticOffers = authenticOfferList.getAuthenticOfferList();
                List<EncryptedOffer> encryptedOffers = encryptedOfferList.getEncryptedOfferList();
                for (AuthenticOffer authenticOffer : authenticOffers) {
                    for (EncryptedOffer encryptedOffer : encryptedOffers) {
                        if (encryptedOffer.getId().equals(authenticOffer.getId())) {
                            String id = encryptedOffer.getId();
                            String status = encryptedOffer.getStatus();
                            String number = encryptedOffer.getNumber();
                            String price = encryptedOffer.getPrice();
                            String aNumber = authenticOffer.getNumber();
                            String aPrice = authenticOffer.getPrice();
                            String salt = authenticOffer.getSalt();
                            String aNumberEncrypt = MD5Utils.getMD5(aNumber, salt);
                            String aPriceEncrypt = MD5Utils.getMD5(aPrice, salt);
                            if (number.equals(aNumberEncrypt) && price.equals(aPriceEncrypt)) {
                                QualifiedOffer qualifiedOffer = new QualifiedOffer();
                                qualifiedOffer.setId(id);
                                qualifiedOffer.setPrice(aPrice);
                                qualifiedOffer.setNumber(aNumber);
                                qualifiedOffer.setStatus(status);
                                qualifiedOfferList.getQualifiedOfferList().add(qualifiedOffer);
                            }
                        }
                    }
                }
                stub.putStringState(q_key, JsonUtils.object2Json(qualifiedOfferList));
                authenticOfferList.setFlag(FLAG_FALSE);
                encryptedOfferList.setFlag(FLAG_FALSE);
                stub.putStringState(e_key, JsonUtils.object2Json(encryptedOfferList));
                stub.putStringState(a_key, JsonUtils.object2Json(authenticOfferList));
            } else {
                return newErrorResponse("请先进行两次报价后再来验证报价！");
            }
            return newSuccessResponse("真实报价成功!", qualifiedOfferList.toString().getBytes(CHARSET));
        } else {
            return newErrorResponse("请求参数不合法：参数只能有一个");
        }
    }

    protected synchronized Chaincode.Response getQualifiedOfferList(ChaincodeStub stub, List<String> args) throws Exception {
        if (args.size() == 1) {
            String offer_id = args.get(0);
            String q_key = createKey(stub, KEY_PREFIX_QUALIFIED_OFFER_LIST, offer_id);
            String value = stub.getStringState(q_key);
//            QualifiedOfferList qualifiedOfferList = getChainCodeObject(stub, q_key, QualifiedOfferList.class);
            return newSuccessResponse("获取有效报价成功!", value.getBytes(CHARSET));
        } else {
            return newErrorResponse("请求参数不合法：参数只能有一个");
        }
    }

    protected <T> T getChainCodeObject(ChaincodeStub stub, String key, Class<T> clazz) throws Exception {
        String value = stub.getStringState(key);
        return JsonUtils.json2Object(value, clazz);
    }

    protected String createKey(ChaincodeStub stub, String pre, String id) {
        return stub.createCompositeKey(pre, id).toString();
    }


    public static void main(String[] args) {
        LOGGER.info(">>> Chaincode starting, args = {}", Arrays.toString(args));
        new BankMasterChaincode().start(args);
    }

}

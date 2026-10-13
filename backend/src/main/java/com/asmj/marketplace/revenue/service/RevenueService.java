package com.asmj.marketplace.revenue.service;

import com.asmj.marketplace.revenue.model.*;
import com.asmj.marketplace.revenue.repository.*;
import com.asmj.marketplace.payment.model.PaymentTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service @RequiredArgsConstructor
public class RevenueService {
 private final CommissionRuleRepository rules;
 private final FeeLedgerRepository ledger;
 private static final List<String> CATEGORIES=List.of("PRODUCT","SERVICE","RENTAL","EVENT","JOB","TUTOR","INSURANCE","PRINTout","OTHER");
 public List<CommissionRule> rules(){ return rules.findAll().stream().sorted(Comparator.comparing(CommissionRule::getCategory,Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))).toList(); }
 public CommissionRule saveRule(String actor, CommissionRule input){
  if(input.getCategory()==null||input.getCategory().isBlank()) throw new IllegalArgumentException("Category is required");
  String category=input.getCategory().trim().toUpperCase(Locale.ROOT);
  String type=input.getFeeType()==null?"PERCENTAGE":input.getFeeType().toUpperCase(Locale.ROOT);
  String payer=input.getFeePayer()==null?"SUPPLIER":input.getFeePayer().toUpperCase(Locale.ROOT);
  if(!Set.of("PERCENTAGE","FIXED","BOTH").contains(type)) throw new IllegalArgumentException("Fee type must be PERCENTAGE, FIXED or BOTH");
  if(!Set.of("SUPPLIER","CUSTOMER","SPLIT").contains(payer)) throw new IllegalArgumentException("Fee payer must be SUPPLIER, CUSTOMER or SPLIT");
  if(!Double.isFinite(input.getPercentage())||input.getPercentage()<0||input.getPercentage()>100) throw new IllegalArgumentException("Percentage must be between 0 and 100");
  if(!Double.isFinite(input.getFixedAmount())||input.getFixedAmount()<0) throw new IllegalArgumentException("Fixed amount cannot be negative");
  CommissionRule r=rules.findByCategoryIgnoreCase(category).orElse(CommissionRule.builder().category(category).build());
  r.setEnabled(input.isEnabled());r.setFeeType(type);r.setPercentage(input.getPercentage());r.setFixedAmount(input.getFixedAmount());r.setFeePayer(payer);r.setUpdatedAt(Instant.now());r.setUpdatedBy(actor);
  return rules.save(r);
 }
 public Map<String,Object> preview(String category,double amount){
  if(!Double.isFinite(amount)||amount<0) throw new IllegalArgumentException("Amount must be zero or greater");
  CommissionRule r=rules.findByCategoryIgnoreCase(category==null?"OTHER":category.trim()).orElse(null);
  double fee=0;
  if(r!=null&&r.isEnabled()) fee=calc(r,amount);
  String payer=r==null?"SUPPLIER":r.getFeePayer();
  double customer="CUSTOMER".equals(payer)?fee:"SPLIT".equals(payer)?round(fee/2):0;
  double supplier=round(fee-customer);
  Map<String,Object> out=new LinkedHashMap<>();out.put("category",category==null?"OTHER":category.toUpperCase(Locale.ROOT));out.put("transactionAmount",round(amount));out.put("ruleEnabled",r!=null&&r.isEnabled());out.put("feeType",r==null?"PERCENTAGE":r.getFeeType());out.put("percentage",r==null?0:r.getPercentage());out.put("fixedAmount",r==null?0:r.getFixedAmount());out.put("feePayer",payer);out.put("platformFee",round(fee));out.put("customerFee",customer);out.put("supplierFee",supplier);out.put("supplierNetBeforeOtherDeductions",round(amount-supplier));return out;
 }
 public FeeLedgerEntry recordVerifiedPayment(PaymentTransaction p){
  if(p==null||p.getId()==null||"REFUND".equalsIgnoreCase(p.getPaymentType())) return null;
  if(ledger.findByPaymentTransactionId(p.getId()).isPresent()) return ledger.findByPaymentTransactionId(p.getId()).orElse(null);
  String category=normalize(p.getBookingType()); CommissionRule r=rules.findByCategoryIgnoreCase(category).orElse(null);
  if(r==null||!r.isEnabled()) return null;
  double fee=calc(r,p.getAmount()); String payer=r.getFeePayer();
  double customer="CUSTOMER".equals(payer)?fee:"SPLIT".equals(payer)?round(fee/2):0; double supplier=round(fee-customer);
  return ledger.save(FeeLedgerEntry.builder().paymentTransactionId(p.getId()).transactionNumber(p.getTransactionNumber()).bookingId(p.getBookingId()).category(category).customerId(p.getPayerId()).supplierId(p.getVendorId()).transactionAmount(round(p.getAmount())).percentage(r.getPercentage()).fixedAmount(r.getFixedAmount()).platformFee(round(fee)).customerFee(customer).supplierFee(supplier).feePayer(payer).status("CALCULATED").createdAt(Instant.now()).build());
 }
 public List<FeeLedgerEntry> ledger(){return ledger.findAllByOrderByCreatedAtDesc();}
 public Map<String,Object> summary(){List<FeeLedgerEntry> all=ledger();double fees=all.stream().filter(x->!Set.of("WAIVED","REVERSED").contains(x.getStatus())).mapToDouble(FeeLedgerEntry::getPlatformFee).sum();double collected=all.stream().filter(x->"COLLECTED".equals(x.getStatus())).mapToDouble(FeeLedgerEntry::getPlatformFee).sum();double calculated=all.stream().filter(x->"CALCULATED".equals(x.getStatus())).mapToDouble(FeeLedgerEntry::getPlatformFee).sum();return Map.of("enabledRules",rules.findAll().stream().filter(CommissionRule::isEnabled).count(),"feeEntries",all.size(),"totalPlatformFees",round(fees),"calculatedNotCollected",round(calculated),"collectedPlatformFees",round(collected),"note","Calculated fees are estimates from verified payment amounts; they are not proof of collection.");}
 private String normalize(String s){if(s==null)return "OTHER";String t=s.trim().toUpperCase(Locale.ROOT);if(t.equals("ORDER"))return "PRODUCT";if(t.equals("PRINTOUT"))return "PRINTOUT";return Set.of("PRODUCT","SERVICE","RENTAL","EVENT","JOB","TUTOR","INSURANCE").contains(t)?t:"OTHER";}
 private double calc(CommissionRule r,double amount){double f=0;if("PERCENTAGE".equals(r.getFeeType())||"BOTH".equals(r.getFeeType()))f+=amount*r.getPercentage()/100;if("FIXED".equals(r.getFeeType())||"BOTH".equals(r.getFeeType()))f+=r.getFixedAmount();return round(Math.min(amount,f));}
 private double round(double v){return Math.round(v*100.0)/100.0;}
}

package com.android.mobgage.data;

import java.util.ArrayList;

public class Proposal {
    public String userID;
    public String proposalID;
    public int proposalNum;
    public int bank;
    public float mortgageAmount;
    public int years;
    public float totalRepayment;
    public float monthRepayment;
    public int myMortgage;        //	1 - mine,  0 - not mine

    private ArrayList<Route> routes = new ArrayList<Route>();

    public Proposal(String userID, String proposalID, int proposalNum,
                    int bank, float mortgageAmount, int years, float totalRepayment,
                    float monthRepayment, int myMortgage) {
        this.userID = userID;
        this.proposalID = proposalID;
        this.proposalNum = proposalNum;
        this.bank = bank;
        this.mortgageAmount = mortgageAmount;
        this.years = years;
        this.totalRepayment = totalRepayment;
        this.monthRepayment = monthRepayment;
        this.myMortgage = myMortgage;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    public void addOrUpdateRoute(Route route) {
        Route r = isRouteExist(route.routeNum);
        if (r == null) {
            routes.add(route);
        } else {
            updateRoute(route, r);
        }
    }

    public Route isRouteExist(int routeNum) {
        for (Route route : routes) {
            if (route.routeNum == routeNum) {
                return route;
            }
        }
        return null;
    }

    private void updateRoute(Route from, Route to) {
        to.userID = from.userID;
        to.proposalID = from.proposalID;
        to.routeNum = from.routeNum;
        to.loanAmount = from.loanAmount;
        to.years = from.years;
        to.interest = from.interest;
        to.monthRepayment = from.monthRepayment;
        to.returnMethod = from.returnMethod;
        to.routeKind = from.routeKind;
        to.totalRepayment = from.totalRepayment;
    }

    public static Proposal copyProposal(Proposal proposal) {
        Proposal copy = new Proposal(proposal.userID, proposal.proposalID, proposal.proposalNum, proposal.bank,
                proposal.mortgageAmount, proposal.years, proposal.totalRepayment, proposal.monthRepayment, proposal.myMortgage);
        for (Route route : proposal.getRoutes()) {
            copy.getRoutes().add(route.copyRoute());
        }
        return copy;
    }

    public double getAllRoutesAmountSum() {
        double sum = 0;
        for (Route r : routes) {
            sum += r.loanAmount;
        }
        return sum;
    }

    public double getAllRoutesMonthRepaymentSum() {
        double sum = 0;
        for (Route r : routes) {
            sum += r.monthRepayment;
        }
        return sum;
    }

    public int getRoutePositionByRouteNum(int routeNum) {
        for (Route route : routes) {
            if (route.routeNum == routeNum) {
                return (routes.indexOf(route)) + 1;
            }
        }
        return routes.size() + 1;
    }

    public int getMaxYears() {
        int max = 0;
        for (Route r : routes) {
            max = (r.years > max ? r.years : max);
        }
        return max;
    }

    public double getTotalRepaymentSum() {
        double sum = 0;
        for (Route r : routes) {
            sum += r.totalRepayment;
        }
        return sum;
    }
}

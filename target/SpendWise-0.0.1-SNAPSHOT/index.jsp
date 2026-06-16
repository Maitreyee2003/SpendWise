<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // 🚪 [SECURITY GUARD CHECK]: Ensures unauthorized requests bounce back to login
    if (session == null || session.getAttribute("userId") == null) {
        response.sendRedirect("login.html");
        return; 
    }
    String userName = (String) session.getAttribute("userName");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SpendWise - Personal Financial Command Center</title>
    <link rel="stylesheet" href="style.css">
    
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf-autotable/3.5.28/jspdf.plugin.autotable.min.js"></script>

    <style>
        /* 🚨 [CRITICAL CHANGE - CONTAINER UNLOCK]: Pehle max-width bohot kam thi jisse space khali lag raha tha.
           Humne use badhakar 1150px kiya taaki dashboard ek luxury wide-screen interface ban sake. */
        .container {
            max-width: 1150px !important; 
            width: 95% !important;
            margin: 30px auto !important;
            padding: 25px !important;
            background: rgba(16, 22, 42, 0.95) !important; /* Premium deep galactic background */
            border-radius: 16px !important;
            border: 1px solid rgba(255, 255, 255, 0.05) !important;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.5) !important;
        }

        /* 📊 [HORIZONTAL BALANCED GRID MATRIX ROW]: Teeno metrics cards ka dynamic alignment system */
        .financial-summary-row {
            display: flex !important;
            flex-direction: row !important;
            justify-content: space-between !important;
            align-items: stretch !important;
            gap: 15px !important;
            margin: 20px 0 25px 0 !important;
            width: 100% !important;
        }

        /* 🟢 [UNIFORM DESIGN METRICS CARDS] */
        .mini-glow-card {
            flex: 1 !important;
            padding: 18px 15px !important;
            border-radius: 12px !important;
            text-align: center !important;
            border: 1px solid rgba(255, 255, 255, 0.07) !important;
            background: rgba(255, 255, 255, 0.02) !important;
            box-sizing: border-box !important;
            min-height: 95px !important;
            display: flex !important;
            flex-direction: column !important;
            justify-content: center !important;
            align-items: center !important;
        }

        .card-label {
            font-size: 11px !important;
            color: #8a99ad !important;
            text-transform: uppercase !important;
            letter-spacing: 1px !important;
            margin-bottom: 8px !important;
            font-weight: 600 !important;
        }

        .card-value {
            font-size: 22px !important; /* Visual clarity badhane ke liye font size expand kiya */
            font-weight: 800 !important;
            margin: 0 !important;
            line-height: 1.2 !important;
        }

        /* 🛠️ [TWO-COLUMN ARCHITECTURE SPLITTER GRID]: 
           Kyun banaya?: Khali space utilize karne ke liye pure dashboard ko do parts me divide kar diya.
           Left Column = Data inputs aur main status counters.
           Right Column = Dynamic real-time transaction ledger aur graphical chart analytics. */
        .workspace-grid {
            display: grid !important;
            grid-template-columns: 1.2fr 1fr !important; /* Left space thoda bada, right symmetric split */
            gap: 25px !important;
            margin-top: 20px !important;
            align-items: start !important;
        }

        /* Forms internal panel items uniform sizing */
        .form-panel-box {
            background: rgba(255, 255, 255, 0.01) !important;
            padding: 20px !important;
            border-radius: 12px !important;
            border: 1px solid rgba(255, 255, 255, 0.04) !important;
            margin-bottom: 20px !important;
        }

        /* Dynamic Pop-up overlay layout styles */
        .income-modal-overlay {
            position: fixed;
            top: 0; left: 0; width: 100%; height: 100%;
            background: rgba(8, 12, 24, 0.85);
            backdrop-filter: blur(8px);
            display: flex; justify-content: center; align-items: center;
            z-index: 2000; opacity: 0; pointer-events: none;
            transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        }
        .income-modal-overlay.active { opacity: 1; pointer-events: auto; }
        .income-modal-box {
            background: rgba(20, 28, 52, 0.98);
            border: 2px solid rgba(0, 198, 255, 0.2);
            box-shadow: 0 0 30px rgba(0, 198, 255, 0.2);
            padding: 30px; border-radius: 16px; width: 340px; text-align: center;
            transform: scale(0.7) translateY(-50px);
            transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        }
        .income-modal-overlay.active .income-modal-box { transform: scale(1) translateY(0); }

        /* Responsive adaptation parameters for table screens */
        @media (max-width: 900px) {
            .workspace-grid { grid-template-columns: 1fr !important; }
            .financial-summary-row { flex-wrap: wrap !important; }
        }
        @media (max-width: 500px) {
            .financial-summary-row { flex-direction: column !important; }
        }
    </style>
</head>
<body>
    <div class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 1px solid rgba(255,255,255,0.05); padding-bottom: 15px;">
            <span style="color: #00c6ff; font-weight: bold; font-size: 15px; letter-spacing: 0.5px;">Financial Control Center // Welcome, <%= userName %>! 👋</span>
            <a href="logoutUser" style="color: #ff4757; text-decoration: none; font-weight: bold; font-size: 12px; border: 1px solid #ff4757; padding: 5px 14px; border-radius: 6px; background: rgba(255, 71, 87, 0.08); transition: all 0.3s;">
                Logout 🚪
            </a>
        </div>

        <h1 style="margin-bottom: 5px; text-align: left; font-size: 28px;">SpendWise</h1>
        <p class="subtitle" style="text-align: left; margin-bottom: 25px;">Enterprise Grade 12-Month Resource Planner</p>

        <div class="financial-summary-row">
            <div id="totalSpentCard" class="mini-glow-card" style="background: linear-gradient(135deg, #1e3c72, #2a5298);">
                <div class="card-label">Expenses Total</div>
                <div id="totalSpent" class="card-value">₹0.00</div>
            </div>
            
            <div id="totalIncomeCard" class="mini-glow-card" style="background: linear-gradient(135deg, #0f2027, #203a43); border-color: rgba(56, 239, 125, 0.25);">
                <div class="card-label" style="color: #38ef7d;">Monthly Income</div>
                <div id="totalIncome" class="card-value" style="color: #38ef7d;">₹0.00</div>
            </div>

            <div id="netSavingsCard" class="mini-glow-card" style="background: linear-gradient(135deg, #0f2027, #203a43); border-color: rgba(0, 198, 255, 0.25);">
                <div class="card-label" style="color: #00c6ff;">Net Savings</div>
                <div id="netSavings" class="card-value" style="color: #00c6ff;">₹0.00</div>
            </div>
        </div>

        <div class="workspace-grid">
            
            <div>
                <div class="form-panel-box" style="display: flex; justify-content: space-between; align-items: center; gap: 10px; flex-wrap: wrap;">
                    <div style="display: flex; align-items: center; gap: 8px;">
                        <label for="monthFilter" style="color: #00c6ff; font-weight: bold; font-size: 13px;">Month:</label>
                        <select id="monthFilter" class="category-select" style="width: auto; padding: 6px 12px; margin-bottom:0; font-size: 13px;">
                            <option value="1">January ❄️</option>
                            <option value="2">February 💖</option>
                            <option value="3">March 🌸</option>
                            <option value="4">April ☀️</option>
                            <option value="5">May 🔥</option>
                            <option value="6">June 🌧️</option>
                            <option value="7">July 🌦️</option>
                            <option value="8">August ☀️</option>
                            <option value="9">September 🍃</option>
                            <option value="10">October 🍂</option>
                            <option value="11">November 🍁</option>
                            <option value="12">December 🎄</option>
                        </select>
                    </div>
                    <div style="display: flex; gap: 10px;">
                        <button id="openIncomeModalBtn" style="background: linear-gradient(135deg, #11998e, #38ef7d); color: white; font-weight: bold; padding: 8px 14px; border: none; border-radius: 6px; cursor: pointer; font-size: 12px; transition: all 0.3s;">Update Income 💼</button>
<!-- Find this button and update href like this -->
<a href="downloadReport?month=January">
    <button>Download Report</button>
</a>                    </div>
                </div>

                <div class="form-panel-box">
                    <div style="display: flex; justify-content: space-between; align-items: center; gap: 10px; flex-wrap: wrap; margin-bottom: 5px;">
                        <label for="monthlyBudgetInput" style="color: #aaa; font-size: 13px; font-weight: bold;">Set Monthly Target Budget Threshold:</label>
                        <input type="number" id="monthlyBudgetInput" placeholder="Enter Budget" style="width: 150px; padding: 6px 12px; margin-bottom: 0; background: rgba(0,0,0,0.3); border: 1px solid rgba(255,255,255,0.1); border-radius: 6px; color: #fff; text-align: center; font-weight: bold; font-size:13px;">
                    </div>
                    <div id="budgetLimitDisplay" style="font-size: 12px; color: #00c6ff; font-weight: bold; text-align: left;">Active Limit for Selected Month: ₹0.00</div>
                </div>

                <div id="budgetAlertBanner" style="display: none; padding: 12px; margin-bottom: 20px; border-radius: 8px; font-size: 12px; font-weight: bold; text-align: center; transition: all 0.3s ease-in-out;"></div>

                <div class="form-panel-box" style="margin-bottom: 25px;">
                    <h3 style="color: #00c6ff; margin-top: 0; font-size: 14px; margin-bottom: 15px; text-transform: uppercase; letter-spacing: 0.5px;">➕Fresh Expense Asset Entry</h3>
                    <div class="input-group" style="display: flex; flex-direction: column; gap: 12px;">
                        <input type="text" id="itemName" placeholder="Item Name Description (e.g., Petrol)" style="margin-bottom:0; width:100%; box-sizing:border-box;">
                        <div style="display: flex; gap: 10px; width: 100%;">
                            <input type="number" id="itemPrice" placeholder="Price Amount (₹)" style="margin-bottom:0; flex:1; box-sizing:border-box;">
                            <select id="itemCategory" class="category-select" style="margin-bottom:0; flex:1.2; box-sizing:border-box;">
                                <option value="Food">Food 🍔</option>
                                <option value="Daily Uses">Daily Uses 🛒</option>
                                <option value="Shopping">Shopping 🛍️</option>
                                <option value="Travel & Fuel">Travel & Fuel 🚗</option>
                                <option value="Bills & Recharge">Bills & Recharge 💡</option>
                                <option value="Others">Others 💼</option>
                            </select>
                        </div>
                        <button id="addBtn" style="width: 100%; padding: 10px; margin-top: 5px; font-weight: bold; letter-spacing: 0.5px;">Add Transaction Asset</button>
                    </div>
                </div>

                <div class="category-summary-grid" style="grid-template-columns: repeat(3, 1fr) !important; gap: 10px !important;">
                    <div class="category-card food-card" style="padding:10px; font-size:11px;">Food:<br><span id="card-food" style="font-size:13px; font-weight:bold;">₹0.00</span></div>
                    <div class="category-card daily-card" style="padding:10px; font-size:11px;">Daily Uses:<br><span id="card-daily" style="font-size:13px; font-weight:bold;">₹0.00</span></div>
                    <div class="category-card shopping-card" style="padding:10px; font-size:11px;">Shopping:<br><span id="card-shopping" style="font-size:13px; font-weight:bold;">₹0.00</span></div>
                </div>
                <div class="category-summary-grid" style="grid-template-columns: repeat(3, 1fr) !important; gap: 10px !important; margin-top: 10px; margin-bottom: 15px;">
                    <div class="category-card travel-card" style="padding:10px; font-size:11px;">Travel & Fuel:<br><span id="card-travel" style="font-size:13px; font-weight:bold;">₹0.00</span></div>
                    <div class="category-card bills-card" style="padding:10px; font-size:11px;">Bills & Recharge:<br><span id="card-bills" style="font-size:13px; font-weight:bold;">₹0.00</span></div>
                    <div class="category-card others-card" style="padding:10px; font-size:11px;">Others:<br><span id="card-others" style="font-size:13px; font-weight:bold;">₹0.00</span></div>
                </div>
            </div>

            <div>
                <div id="chartContainer" style="background: rgba(255, 255, 255, 0.02); padding: 15px; border-radius: 12px; border: 1px solid rgba(255, 255, 255, 0.05); margin-bottom: 20px; display: none; box-sizing: border-box;">
                    <canvas id="expenseChart" style="max-height: 190px; max-width: 100%; margin: 0 auto;"></canvas>
                </div>

                <div class="transactions" style="margin-top: 0; background: rgba(0,0,0,0.15) !important; border: 1px solid rgba(255,255,255,0.03) !important; border-radius: 12px !important; padding: 15px !important; max-height: 480px; overflow-y: auto;">
                    <h3 style="font-size: 14px; text-transform: uppercase; letter-spacing: 0.5px; color: #00c6ff; margin-top: 0; margin-bottom: 15px; border-bottom: 1px solid rgba(255,255,255,0.05); padding-bottom: 8px;">📋 Recent Transaction Ledger Logs</h3>
                    <div id="transactionList"></div>
                </div>
            </div>

        </div>

        <div style="margin: 30px 0 10px 0; border-top: 1px solid rgba(255,255,255,0.05); padding-top: 20px;">
            <h4 style="color: #00c6ff; margin-bottom: 12px; text-align: center; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;">📊 Annual Overview Breakdown (Macro Matrix Stats Grid)</h4>
            <div style="display: grid; grid-template-columns: repeat(6, 1fr); gap: 10px; margin-bottom: 10px;">
                <div style="background: rgba(255,255,255,0.02); padding: 8px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.04); text-align: center; font-size: 11px;"><div style="color: #aaa;">Jan</div><strong id="m-1" style="color: #00c6ff;">₹0.00</strong></div>
                <div style="background: rgba(255,255,255,0.02); padding: 8px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.04); text-align: center; font-size: 11px;"><div style="color: #aaa;">Feb</div><strong id="m-2" style="color: #00c6ff;">₹0.00</strong></div>
                <div style="background: rgba(255,255,255,0.02); padding: 8px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.04); text-align: center; font-size: 11px;"><div style="color: #aaa;">Mar</div><strong id="m-3" style="color: #00c6ff;">₹0.00</strong></div>
                <div style="background: rgba(255,255,255,0.02); padding: 8px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.04); text-align: center; font-size: 11px;"><div style="color: #aaa;">Apr</div><strong id="m-4" style="color: #00c6ff;">₹0.00</strong></div>
                <div style="background: rgba(255,255,255,0.02); padding: 8px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.04); text-align: center; font-size: 11px;"><div style="color: #aaa;">May</div><strong id="m-5" style="color: #00c6ff;">₹0.00</strong></div>
                <div style="background: rgba(255,255,255,0.02); padding: 8px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.04); text-align: center; font-size: 11px;"><div style="color: #aaa;">Jun</div><strong id="m-6" style="color: #00c6ff;">₹0.00</strong></div>
            </div>
            <div style="display: grid; grid-template-columns: repeat(6, 1fr); gap: 10px;">
                <div style="background: rgba(255,255,255,0.02); padding: 8px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.04); text-align: center; font-size: 11px;"><div style="color: #aaa;">Jul</div><strong id="m-7" style="color: #00c6ff;">₹0.00</strong></div>
                <div style="background: rgba(255,255,255,0.02); padding: 8px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.04); text-align: center; font-size: 11px;"><div style="color: #aaa;">Aug</div><strong id="m-8" style="color: #00c6ff;">₹0.00</strong></div>
                <div style="background: rgba(255,255,255,0.02); padding: 8px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.04); text-align: center; font-size: 11px;"><div style="color: #aaa;">Sep</div><strong id="m-9" style="color: #00c6ff;">₹0.00</strong></div>
                <div style="background: rgba(255,255,255,0.02); padding: 8px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.04); text-align: center; font-size: 11px;"><div style="color: #aaa;">Oct</div><strong id="m-10" style="color: #00c6ff;">₹0.00</strong></div>
                <div style="background: rgba(255,255,255,0.02); padding: 8px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.04); text-align: center; font-size: 11px;"><div style="color: #aaa;">Nov</div><strong id="m-11" style="color: #00c6ff;">₹0.00</strong></div>
                <div style="background: rgba(255,255,255,0.02); padding: 8px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.04); text-align: center; font-size: 11px;"><div style="color: #aaa;">Dec</div><strong id="m-12" style="color: #00c6ff;">₹0.00</strong></div>
            </div>
        </div>

    </div>

    <div id="incomeModal" class="income-modal-overlay">
        <div class="income-modal-box">
            <h3 style="color: #38ef7d; margin-top: 0; font-size: 17px; margin-bottom: 10px;">Update Monthly Income 💼</h3>
            <p style="color: #aaa; font-size: 11px; margin-bottom: 20px;">Enter your absolute earnings or salary parameters for this tracking month.</p>
            <input type="number" id="modalIncomeInput" placeholder="Amount (e.g., 50000)" style="width: 100%; padding: 10px; background: rgba(0,0,0,0.4); border: 1px solid rgba(56, 239, 125, 0.3); border-radius: 8px; color: #fff; text-align: center; font-size: 16px; font-weight: bold; margin-bottom: 20px; box-sizing: border-box;">
            <div style="display: flex; gap: 10px; justify-content: center;">
                <button id="closeIncomeModalBtn" style="background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); color: #ccc; padding: 8px 16px; border-radius: 6px; cursor: pointer; font-weight: bold; font-size: 12px;">Cancel</button>
                <button id="saveIncomeModalBtn" style="background: linear-gradient(135deg, #11998e, #38ef7d); border: none; color: white; padding: 8px 20px; border-radius: 6px; cursor: pointer; font-weight: bold; font-size: 12px; box-shadow: 0 4px 12px rgba(56, 239, 125, 0.2);">Save Income</button>
            </div>
        </div>
    </div>

    <script>
        window.currentUserName = "<%= userName %>";
    </script>
    <script src="script.js"></script>
</body>
</html>
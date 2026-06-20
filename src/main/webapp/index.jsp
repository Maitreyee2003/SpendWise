<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
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
    <title>SpendWise - Financial Dashboard</title>
    <link rel="stylesheet" href="style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf-autotable/3.5.28/jspdf.plugin.autotable.min.js"></script>

    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap');

        * { margin: 0; padding: 0; box-sizing: border-box; }

        :root {
            --cyan: #00c6ff;
            --blue: #0072ff;
            --green: #38ef7d;
            --red: #ff4757;
            --orange: #ffa502;
            --purple: #7c5cbf;
            --dark: #060910;
            --dark2: #0d1117;
            --dark3: #161b22;
            --dark4: #1c2128;
            --border: rgba(255,255,255,0.06);
            --border2: rgba(255,255,255,0.1);
            --text1: #f0f6fc;
            --text2: #8b949e;
            --text3: #484f58;
        }

        body {
            font-family: 'Inter', sans-serif;
            background: var(--dark);
            color: var(--text1);
            min-height: 100vh;
            overflow-x: hidden;
        }

        /* ======= ANIMATED BACKGROUND ======= */
        .bg-canvas {
            position: fixed;
            top: 0; left: 0;
            width: 100%; height: 100%;
            z-index: 0;
            pointer-events: none;
        }

        .bg-orb {
            position: absolute;
            border-radius: 50%;
            filter: blur(80px);
            animation: orbFloat 12s ease-in-out infinite;
        }

        .orb1 {
            width: 400px; height: 400px;
            background: rgba(0,198,255,0.06);
            top: -100px; left: -100px;
            animation-delay: 0s;
        }

        .orb2 {
            width: 350px; height: 350px;
            background: rgba(0,114,255,0.06);
            top: 30%; right: -100px;
            animation-delay: -4s;
        }

        .orb3 {
            width: 300px; height: 300px;
            background: rgba(56,239,125,0.04);
            bottom: 10%; left: 30%;
            animation-delay: -8s;
        }

        @keyframes orbFloat {
            0%, 100% { transform: translate(0, 0) scale(1); }
            33% { transform: translate(30px, -30px) scale(1.05); }
            66% { transform: translate(-20px, 20px) scale(0.95); }
        }

        /* Grid lines */
        .bg-grid {
            position: fixed;
            top: 0; left: 0;
            width: 100%; height: 100%;
            background-image:
                linear-gradient(rgba(255,255,255,0.02) 1px, transparent 1px),
                linear-gradient(90deg, rgba(255,255,255,0.02) 1px, transparent 1px);
            background-size: 50px 50px;
            z-index: 0;
            pointer-events: none;
        }

        /* ======= LAYOUT ======= */
        .app {
            position: relative;
            z-index: 1;
            max-width: 1280px;
            width: 95%;
            margin: 0 auto;
            padding: 20px 0 60px;
        }

        /* ======= NAVBAR ======= */
        .navbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 14px 20px;
            background: rgba(13,17,23,0.8);
            border: 1px solid var(--border);
            border-radius: 16px;
            margin-bottom: 28px;
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            position: sticky;
            top: 10px;
            z-index: 100;
        }

        .nav-brand {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .nav-logo {
            width: 38px; height: 38px;
            background: linear-gradient(135deg, var(--cyan), var(--blue));
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 18px;
            box-shadow: 0 0 20px rgba(0,198,255,0.2);
            animation: logoPulse 3s ease-in-out infinite;
        }

        @keyframes logoPulse {
            0%, 100% { box-shadow: 0 0 20px rgba(0,198,255,0.2); }
            50% { box-shadow: 0 0 30px rgba(0,198,255,0.4); }
        }

        .nav-text .brand-name {
            font-size: 16px;
            font-weight: 800;
            background: linear-gradient(135deg, var(--cyan), var(--blue));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            letter-spacing: 2px;
        }

        .nav-text .brand-sub {
            font-size: 10px;
            color: var(--text3);
            letter-spacing: 1px;
        }

        .nav-right {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .nav-badge {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 7px 14px;
            background: rgba(0,198,255,0.06);
            border: 1px solid rgba(0,198,255,0.12);
            border-radius: 10px;
        }

        .nav-avatar {
            width: 26px; height: 26px;
            background: linear-gradient(135deg, var(--cyan), var(--blue));
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 11px;
            font-weight: 700;
        }

        .nav-username {
            font-size: 13px;
            font-weight: 600;
            color: var(--cyan);
        }

        .logout-link {
            display: flex;
            align-items: center;
            gap: 6px;
            padding: 7px 14px;
            background: rgba(255,71,87,0.06);
            border: 1px solid rgba(255,71,87,0.15);
            border-radius: 10px;
            color: var(--red);
            font-size: 12px;
            font-weight: 700;
            text-decoration: none;
            transition: all 0.3s;
            letter-spacing: 0.5px;
        }

        .logout-link:hover {
            background: rgba(255,71,87,0.12);
            border-color: rgba(255,71,87,0.3);
            transform: translateY(-1px);
        }

        /* ======= STATS CARDS ======= */
        /*force stats horizontal*/
        .stats-grid {
            display: grid !important;
            grid-template-columns: repeat(3, 1fr) !important;
            gap: 16px !important;
            margin-bottom: 24px !important;
        }

	/*force body display fix*/
	body{
	display: block !important;
	}
        .stat-card {
            position: relative;
            padding: 22px 24px;
            background: var(--dark3);
            border: 1px solid var(--border);
            border-radius: 16px;
            overflow: hidden;
            cursor: default;
            transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        }

        .stat-card:hover {
            transform: translateY(-4px);
            border-color: var(--border2);
        }

        .stat-card::before {
            content: '';
            position: absolute;
            top: 0; left: 0;
            width: 100%; height: 2px;
            transition: opacity 0.3s;
        }

        .stat-card::after {
            content: '';
            position: absolute;
            top: -60px; right: -60px;
            width: 140px; height: 140px;
            border-radius: 50%;
            opacity: 0.06;
            transition: all 0.4s;
        }

        .stat-card:hover::after { opacity: 0.1; transform: scale(1.1); }

        .stat-red::before { background: linear-gradient(90deg, var(--red), transparent); }
        .stat-red::after { background: var(--red); }
        .stat-red:hover { box-shadow: 0 8px 30px rgba(255,71,87,0.1); }

        .stat-green::before { background: linear-gradient(90deg, var(--green), transparent); }
        .stat-green::after { background: var(--green); }
        .stat-green:hover { box-shadow: 0 8px 30px rgba(56,239,125,0.1); }

        .stat-cyan::before { background: linear-gradient(90deg, var(--cyan), transparent); }
        .stat-cyan::after { background: var(--cyan); }
        .stat-cyan:hover { box-shadow: 0 8px 30px rgba(0,198,255,0.1); }

        .stat-icon-wrap {
            position: relative;
            z-index: 1;
            width: 40px; height: 40px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 18px;
            margin-bottom: 14px;
        }

        .stat-red .stat-icon-wrap { background: rgba(255,71,87,0.12); }
        .stat-green .stat-icon-wrap { background: rgba(56,239,125,0.12); }
        .stat-cyan .stat-icon-wrap { background: rgba(0,198,255,0.12); }

        .stat-label {
            position: relative;
            z-index: 1;
            font-size: 10px;
            font-weight: 700;
            letter-spacing: 2px;
            text-transform: uppercase;
            margin-bottom: 6px;
        }

        .stat-red .stat-label { color: rgba(255,71,87,0.7); }
        .stat-green .stat-label { color: rgba(56,239,125,0.7); }
        .stat-cyan .stat-label { color: rgba(0,198,255,0.7); }

        .stat-value {
            position: relative;
            z-index: 1;
            font-size: 28px;
            font-weight: 800;
            letter-spacing: -0.5px;
            line-height: 1;
        }

        .stat-red .stat-value { color: var(--red); }
        .stat-green .stat-value { color: var(--green); }
        .stat-cyan .stat-value { color: var(--cyan); }

        /* Counter animation */
        @keyframes countUp {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .stat-value { animation: countUp 0.6s ease forwards; }

        /* ======= MAIN LAYOUT ======= */
        .workspace {
            display: grid;
            grid-template-columns: 1.15fr 1fr;
            gap: 20px;
            margin-bottom: 24px;
        }

        /* ======= GLASS CARD ======= */
        .glass-card {
            background: rgba(22,27,34,0.8);
            border: 1px solid var(--border);
            border-radius: 16px;
            padding: 20px;
            margin-bottom: 16px;
            backdrop-filter: blur(10px);
            transition: border-color 0.3s;
        }

        .glass-card:last-child { margin-bottom: 0; }
        .glass-card:hover { border-color: var(--border2); }

        .card-header {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 11px;
            font-weight: 700;
            letter-spacing: 2px;
            text-transform: uppercase;
            color: var(--cyan);
            margin-bottom: 16px;
            padding-bottom: 12px;
            border-bottom: 1px solid var(--border);
        }

        .card-header-dot {
            width: 6px; height: 6px;
            border-radius: 50%;
            background: var(--cyan);
            box-shadow: 0 0 8px var(--cyan);
            animation: dotBlink 2s ease-in-out infinite;
        }

        @keyframes dotBlink {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.3; }
        }

        /* ======= CONTROLS ======= */
        .controls-wrap {
            display: flex;
            align-items: center;
            gap: 10px;
            flex-wrap: wrap;
        }

        .month-label {
            font-size: 12px;
            font-weight: 600;
            color: var(--cyan);
        }

        .sw-select {
            background: rgba(255,255,255,0.04);
            border: 1px solid var(--border2);
            color: var(--text1);
            padding: 8px 14px;
            border-radius: 10px;
            font-size: 12px;
            font-family: 'Inter', sans-serif;
            outline: none;
            cursor: pointer;
            transition: all 0.3s;
        }

        .sw-select:focus { border-color: var(--cyan); }
        .sw-select option { background: #161b22; }

        .sw-btn {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 8px 14px;
            border: none;
            border-radius: 10px;
            font-size: 12px;
            font-weight: 700;
            font-family: 'Inter', sans-serif;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
            letter-spacing: 0.3px;
        }

        .btn-green {
            background: linear-gradient(135deg, rgba(17,153,142,0.8), rgba(56,239,125,0.8));
            color: white;
        }

        .btn-green:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(56,239,125,0.2);
        }

        .btn-blue {
            background: linear-gradient(135deg, rgba(0,198,255,0.8), rgba(0,114,255,0.8));
            color: white;
        }

        .btn-blue:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(0,198,255,0.2);
        }

        /* ======= BUDGET ======= */
        .budget-wrap { margin-top: 14px; }

        .budget-row {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 8px;
        }

        .budget-label {
            font-size: 12px;
            font-weight: 500;
            color: var(--text2);
        }

        .budget-input {
            width: 130px;
            padding: 7px 12px;
            background: rgba(0,0,0,0.4);
            border: 1px solid var(--border2);
            border-radius: 8px;
            color: var(--text1);
            font-size: 13px;
            font-weight: 700;
            font-family: 'Inter', sans-serif;
            text-align: center;
            outline: none;
            transition: border-color 0.3s;
        }

        .budget-input:focus { border-color: var(--cyan); }

        .budget-active {
            font-size: 11px;
            color: var(--cyan);
            font-weight: 600;
            margin-bottom: 8px;
        }

        .progress-track {
            height: 6px;
            background: rgba(255,255,255,0.06);
            border-radius: 3px;
            overflow: hidden;
            margin-bottom: 8px;
        }

        .progress-fill {
            height: 100%;
            border-radius: 3px;
            transition: width 0.8s cubic-bezier(0.4, 0, 0.2, 1),
                        background 0.3s;
            background: linear-gradient(90deg, var(--green), var(--cyan));
        }

        #budgetAlertBanner {
            display: none;
            border-radius: 10px;
            padding: 10px 14px;
            font-size: 12px;
            font-weight: 600;
            margin-top: 8px;
            text-align: center;
            animation: fadeSlideIn 0.4s ease;
        }

        @keyframes fadeSlideIn {
            from { opacity: 0; transform: translateY(-6px); }
            to { opacity: 1; transform: translateY(0); }
        }

        /* ======= EXPENSE FORM ======= */
        .form-col {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .sw-input {
            width: 100%;
            padding: 12px 14px;
            background: rgba(255,255,255,0.04);
            border: 1px solid var(--border);
            border-radius: 10px;
            color: var(--text1);
            font-size: 13px;
            font-family: 'Inter', sans-serif;
            outline: none;
            transition: all 0.3s;
        }

        .sw-input:focus {
            border-color: rgba(0,198,255,0.4);
            background: rgba(0,198,255,0.04);
            box-shadow: 0 0 0 3px rgba(0,198,255,0.06);
        }

        .sw-input::placeholder { color: var(--text3); }

        .form-row {
            display: flex;
            gap: 10px;
        }

        .add-btn {
            width: 100%;
            padding: 13px;
            background: linear-gradient(135deg, var(--cyan), var(--blue));
            border: none;
            color: white;
            font-weight: 700;
            font-size: 14px;
            font-family: 'Inter', sans-serif;
            border-radius: 10px;
            cursor: pointer;
            letter-spacing: 0.5px;
            transition: all 0.3s;
            position: relative;
            overflow: hidden;
        }

        .add-btn::after {
            content: '';
            position: absolute;
            top: 50%; left: 50%;
            width: 0; height: 0;
            background: rgba(255,255,255,0.15);
            border-radius: 50%;
            transform: translate(-50%, -50%);
            transition: width 0.4s, height 0.4s;
        }

        .add-btn:active::after {
            width: 300px;
            height: 300px;
        }

        .add-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 24px rgba(0,114,255,0.3);
        }

        /* ======= CATEGORY CARDS ======= */
        .cat-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 8px;
        }

        .cat-card {
            padding: 12px;
            background: rgba(255,255,255,0.02);
            border: 1px solid var(--border);
            border-radius: 10px;
            transition: all 0.3s;
            cursor: default;
        }

        .cat-card:hover {
            transform: translateY(-3px);
            border-color: var(--border2);
        }

        .cat-name {
            font-size: 9px;
            font-weight: 700;
            letter-spacing: 1.5px;
            text-transform: uppercase;
            margin-bottom: 5px;
        }

        .cat-val {
            font-size: 15px;
            font-weight: 800;
        }

        .c-food { border-left: 2px solid #ff4757; }
        .c-food .cat-name { color: rgba(255,71,87,0.7); }
        .c-food .cat-val { color: #ff4757; }

        .c-daily { border-left: 2px solid #1e90ff; }
        .c-daily .cat-name { color: rgba(30,144,255,0.7); }
        .c-daily .cat-val { color: #1e90ff; }

        .c-shop { border-left: 2px solid #eccc68; }
        .c-shop .cat-name { color: rgba(236,204,104,0.7); }
        .c-shop .cat-val { color: #eccc68; }

        .c-travel { border-left: 2px solid #2ed573; }
        .c-travel .cat-name { color: rgba(46,213,115,0.7); }
        .c-travel .cat-val { color: #2ed573; }

        .c-bills { border-left: 2px solid #ffa502; }
        .c-bills .cat-name { color: rgba(255,165,2,0.7); }
        .c-bills .cat-val { color: #ffa502; }

        .c-others { border-left: 2px solid #7c5cbf; }
        .c-others .cat-name { color: rgba(124,92,191,0.7); }
        .c-others .cat-val { color: #7c5cbf; }

        /* ======= CHART ======= */
        .chart-wrap {
            background: rgba(22,27,34,0.8);
            border: 1px solid var(--border);
            border-radius: 16px;
            padding: 20px;
            margin-bottom: 16px;
            display: none;
        }

        /* ======= TRANSACTIONS ======= */
        .txn-wrap {
            background: rgba(22,27,34,0.8);
            border: 1px solid var(--border);
            border-radius: 16px;
            padding: 20px;
            max-height: 420px;
            overflow-y: auto;
        }

        .txn-wrap::-webkit-scrollbar { width: 3px; }
        .txn-wrap::-webkit-scrollbar-thumb {
            background: rgba(0,198,255,0.2);
            border-radius: 2px;
        }

        .transaction-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 14px;
            background: rgba(255,255,255,0.02);
            border: 1px solid var(--border);
            border-left: 2px solid #ff4757;
            border-radius: 10px;
            margin-bottom: 8px;
            transition: all 0.25s;
        }

        .transaction-item:hover {
            background: rgba(255,255,255,0.04);
            transform: translateX(4px);
            border-color: var(--border2);
            border-left-color: #ff4757;
        }

        .txn-name {
            font-size: 13px;
            font-weight: 600;
            color: var(--text1);
        }

        .txn-cat {
            font-size: 11px;
            color: var(--text3);
            margin-top: 2px;
        }

        .txn-right {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .txn-amount {
            font-size: 14px;
            font-weight: 800;
            color: var(--red);
        }

        .txn-del {
            width: 28px; height: 28px;
            background: rgba(255,71,87,0.08);
            border: 1px solid rgba(255,71,87,0.15);
            color: var(--red);
            border-radius: 7px;
            cursor: pointer;
            font-size: 13px;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.2s;
            font-family: sans-serif;
        }

        .txn-del:hover {
            background: rgba(255,71,87,0.16);
            transform: scale(1.1);
        }

        /* ======= ANNUAL GRID ======= */
        .annual-wrap {
            background: rgba(22,27,34,0.8);
            border: 1px solid var(--border);
            border-radius: 16px;
            padding: 20px;
            margin-bottom: 24px;
        }

        .annual-grid {
            display: grid;
            grid-template-columns: repeat(12, 1fr);
            gap: 8px;
        }

        .month-cell {
            background: rgba(255,255,255,0.02);
            border: 1px solid var(--border);
            border-radius: 10px;
            padding: 10px 6px;
            text-align: center;
            transition: all 0.3s;
            cursor: default;
        }

        .month-cell:hover {
            background: rgba(0,198,255,0.06);
            border-color: rgba(0,198,255,0.2);
            transform: translateY(-3px);
            box-shadow: 0 6px 16px rgba(0,0,0,0.3);
        }

        .m-name {
            font-size: 9px;
            font-weight: 700;
            letter-spacing: 1px;
            color: var(--text3);
            text-transform: uppercase;
            margin-bottom: 5px;
        }

        .m-val {
            font-size: 10px;
            font-weight: 700;
            color: var(--cyan);
        }

        /* ======= MODAL ======= */
        .modal-overlay {
            position: fixed;
            inset: 0;
            background: rgba(6,9,16,0.85);
            backdrop-filter: blur(12px);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 1000;
            opacity: 0;
            pointer-events: none;
            transition: opacity 0.3s;
        }

        .modal-overlay.active {
            opacity: 1;
            pointer-events: all;
        }

        .modal-box {
            background: var(--dark3);
            border: 1px solid rgba(56,239,125,0.15);
            box-shadow:
                0 0 0 1px rgba(255,255,255,0.04),
                0 24px 48px rgba(0,0,0,0.6);
            padding: 32px;
            border-radius: 20px;
            width: 360px;
            text-align: center;
            transform: scale(0.85) translateY(-20px);
            transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        }

        .modal-overlay.active .modal-box {
            transform: scale(1) translateY(0);
        }

        .modal-icon {
            width: 56px; height: 56px;
            background: rgba(56,239,125,0.1);
            border: 1px solid rgba(56,239,125,0.2);
            border-radius: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            margin: 0 auto 16px;
        }

        .modal-title {
            color: var(--green);
            font-size: 18px;
            font-weight: 800;
            margin-bottom: 6px;
            letter-spacing: -0.3px;
        }

        .modal-sub {
            color: var(--text3);
            font-size: 12px;
            line-height: 1.6;
            margin-bottom: 20px;
        }

        .modal-input {
            width: 100%;
            padding: 13px;
            background: rgba(0,0,0,0.5);
            border: 1px solid rgba(56,239,125,0.2);
            border-radius: 10px;
            color: var(--text1);
            font-size: 20px;
            font-weight: 800;
            font-family: 'Inter', sans-serif;
            text-align: center;
            margin-bottom: 20px;
            outline: none;
            transition: all 0.3s;
            box-sizing: border-box;
        }

        .modal-input:focus {
            border-color: rgba(56,239,125,0.5);
            box-shadow: 0 0 0 3px rgba(56,239,125,0.08);
        }

        .modal-btns {
            display: flex;
            gap: 10px;
            justify-content: center;
        }

        .modal-cancel {
            padding: 10px 20px;
            background: rgba(255,255,255,0.04);
            border: 1px solid var(--border2);
            color: var(--text2);
            border-radius: 10px;
            cursor: pointer;
            font-weight: 600;
            font-size: 13px;
            font-family: 'Inter', sans-serif;
            transition: all 0.2s;
        }

        .modal-cancel:hover { background: rgba(255,255,255,0.08); }

        .modal-save {
            padding: 10px 24px;
            background: linear-gradient(135deg, #11998e, var(--green));
            border: none;
            color: white;
            border-radius: 10px;
            cursor: pointer;
            font-weight: 700;
            font-size: 13px;
            font-family: 'Inter', sans-serif;
            transition: all 0.2s;
        }

        .modal-save:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(56,239,125,0.25);
        }

        /* ======= ANIMATIONS ======= */
        .fade-in-item {
            animation: fadeItem 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards;
        }

        @keyframes fadeItem {
            from { opacity: 0; transform: translateY(-8px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .empty-msg {
            color: var(--text3);
            font-size: 13px;
            text-align: center;
            padding: 24px 0;
        }

        /* ======= RESPONSIVE ======= */
        @media (max-width: 1000px) {
            .workspace { grid-template-columns: 1fr; }
            .annual-grid { grid-template-columns: repeat(6, 1fr); }
        }

        @media (max-width: 700px) {
            .stats-grid { grid-template-columns: 1fr; }
            .annual-grid { grid-template-columns: repeat(4, 1fr); }
            .navbar { flex-wrap: wrap; gap: 10px; }
        }

        @media (max-width: 500px) {
            .cat-grid { grid-template-columns: repeat(2, 1fr); }
            .annual-grid { grid-template-columns: repeat(3, 1fr); }
        }
    </style>
</head>
<body>

    <!-- Animated Background -->
    <div class="bg-canvas">
        <div class="bg-orb orb1"></div>
        <div class="bg-orb orb2"></div>
        <div class="bg-orb orb3"></div>
    </div>
    <div class="bg-grid"></div>

    <div class="app">

        <!-- ======= NAVBAR ======= -->
        <nav class="navbar">
            <div class="nav-brand">
                <div class="nav-logo">💰</div>
                <div class="nav-text">
                    <div class="brand-name">SPENDWISE</div>
                    <div class="brand-sub">Personal Finance Manager</div>
                </div>
            </div>
            <div class="nav-right">
                <div class="nav-badge">
                    <div class="nav-avatar">
                        <%= userName.substring(0,1).toUpperCase() %>
                    </div>
                    <span class="nav-username"><%= userName %></span>
                </div>
                <a href="logoutUser" class="logout-link">
                    🚪 Logout
                </a>
            </div>
        </nav>

        <!-- ======= STATS ======= -->
        <div class="stats-grid">
            <div class="stat-card stat-red">
                <div class="stat-icon-wrap">💸</div>
                <div class="stat-label">Expenses Total</div>
                <div id="totalSpent" class="stat-value">₹0.00</div>
            </div>
            <div class="stat-card stat-green">
                <div class="stat-icon-wrap">💰</div>
                <div class="stat-label">Monthly Income</div>
                <div id="totalIncome" class="stat-value">₹0.00</div>
            </div>
            <div class="stat-card stat-cyan">
                <div class="stat-icon-wrap">🏦</div>
                <div class="stat-label">Net Savings</div>
                <div id="netSavings" class="stat-value">₹0.00</div>
            </div>
        </div>

        <!-- ======= WORKSPACE ======= -->
        <div class="workspace">

            <!-- LEFT COLUMN -->
            <div>

                <!-- Controls -->
                <div class="glass-card">
                    <div class="card-header">
                        <div class="card-header-dot"></div>
                        Controls
                    </div>
                    <div class="controls-wrap">
                        <span class="month-label">Month:</span>
                        <select id="monthFilter" class="sw-select">
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
                        <button id="openIncomeModalBtn" class="sw-btn btn-green">
                            💼 Income
                        </button>
                        <a href="downloadReport?month=January"
                           class="sw-btn btn-blue"
                           id="downloadReportBtn">
                            📥 Report
                        </a>
                    </div>

                    <!-- Budget -->
                    <div class="budget-wrap">
                        <div class="budget-row">
                            <span class="budget-label">Monthly Budget Limit</span>
                            <input type="number"
                                   id="monthlyBudgetInput"
                                   class="budget-input"
                                   placeholder="₹ Set limit">
                        </div>
                        <div id="budgetLimitDisplay" class="budget-active">
                            Active Limit: ₹0.00
                        </div>
                        <div class="progress-track">
                            <div class="progress-fill"
                                 id="budgetProgressBar"
                                 style="width:0%;">
                            </div>
                        </div>
                        <div id="budgetAlertBanner"></div>
                    </div>
                </div>

                <!-- Add Expense -->
                <div class="glass-card">
                    <div class="card-header">
                        <div class="card-header-dot"></div>
                        Add New Expense
                    </div>
                    <div class="form-col">
                        <input type="text"
                               id="itemName"
                               class="sw-input"
                               placeholder="Item description (e.g., Petrol, Pizza)">
                        <div class="form-row">
                            <input type="number"
                                   id="itemPrice"
                                   class="sw-input"
                                   placeholder="Amount (₹)"
                                   style="flex:1;">
                            <select id="itemCategory"
                                    class="sw-select"
                                    style="flex:1.2;">
                                <option value="Food">Food 🍔</option>
                                <option value="Daily Uses">Daily Uses 🛒</option>
                                <option value="Shopping">Shopping 🛍️</option>
                                <option value="Travel & Fuel">Travel & Fuel 🚗</option>
                                <option value="Bills & Recharge">Bills & Recharge 💡</option>
                                <option value="Others">Others 💼</option>
                            </select>
                        </div>
                        <button id="addBtn" class="add-btn">
                            ➕ Add Transaction
                        </button>
                    </div>
                </div>

                <!-- Category Breakdown -->
                <div class="glass-card">
                    <div class="card-header">
                        <div class="card-header-dot"></div>
                        Category Breakdown
                    </div>
                    <div class="cat-grid">
                        <div class="cat-card c-food">
                            <div class="cat-name">Food</div>
                            <div class="cat-val" id="card-food">₹0.00</div>
                        </div>
                        <div class="cat-card c-daily">
                            <div class="cat-name">Daily Uses</div>
                            <div class="cat-val" id="card-daily">₹0.00</div>
                        </div>
                        <div class="cat-card c-shop">
                            <div class="cat-name">Shopping</div>
                            <div class="cat-val" id="card-shopping">₹0.00</div>
                        </div>
                        <div class="cat-card c-travel">
                            <div class="cat-name">Travel & Fuel</div>
                            <div class="cat-val" id="card-travel">₹0.00</div>
                        </div>
                        <div class="cat-card c-bills">
                            <div class="cat-name">Bills & Recharge</div>
                            <div class="cat-val" id="card-bills">₹0.00</div>
                        </div>
                        <div class="cat-card c-others">
                            <div class="cat-name">Others</div>
                            <div class="cat-val" id="card-others">₹0.00</div>
                        </div>
                    </div>
                </div>

            </div>

            <!-- RIGHT COLUMN -->
            <div>

                <!-- Chart -->
                <div id="chartContainer" class="chart-wrap">
                    <div class="card-header">
                        <div class="card-header-dot"></div>
                        Expense Distribution
                    </div>
                    <canvas id="expenseChart"
                            style="max-height:220px; width:100%;">
                    </canvas>
                </div>

                <!-- Transactions -->
                <div class="txn-wrap">
                    <div class="card-header">
                        <div class="card-header-dot"></div>
                        Recent Transactions
                    </div>
                    <div id="transactionList"></div>
                </div>

            </div>
        </div>

        <!-- ======= ANNUAL GRID ======= -->
        <div class="annual-wrap">
            <div class="card-header" style="justify-content:center; border:none; margin-bottom:14px;">
                <div class="card-header-dot"></div>
                Annual Overview — 12 Month Breakdown
            </div>
            <div class="annual-grid">
                <div class="month-cell"><div class="m-name">Jan</div><div class="m-val" id="m-1">₹0</div></div>
                <div class="month-cell"><div class="m-name">Feb</div><div class="m-val" id="m-2">₹0</div></div>
                <div class="month-cell"><div class="m-name">Mar</div><div class="m-val" id="m-3">₹0</div></div>
                <div class="month-cell"><div class="m-name">Apr</div><div class="m-val" id="m-4">₹0</div></div>
                <div class="month-cell"><div class="m-name">May</div><div class="m-val" id="m-5">₹0</div></div>
                <div class="month-cell"><div class="m-name">Jun</div><div class="m-val" id="m-6">₹0</div></div>
                <div class="month-cell"><div class="m-name">Jul</div><div class="m-val" id="m-7">₹0</div></div>
                <div class="month-cell"><div class="m-name">Aug</div><div class="m-val" id="m-8">₹0</div></div>
                <div class="month-cell"><div class="m-name">Sep</div><div class="m-val" id="m-9">₹0</div></div>
                <div class="month-cell"><div class="m-name">Oct</div><div class="m-val" id="m-10">₹0</div></div>
                <div class="month-cell"><div class="m-name">Nov</div><div class="m-val" id="m-11">₹0</div></div>
                <div class="month-cell"><div class="m-name">Dec</div><div class="m-val" id="m-12">₹0</div></div>
            </div>
        </div>

    </div>

    <!-- ======= INCOME MODAL ======= -->
    <div id="incomeModal" class="modal-overlay">
        <div class="modal-box">
            <div class="modal-icon">💼</div>
            <div class="modal-title">Update Monthly Income</div>
            <p class="modal-sub">
                Enter your salary or total earnings<br>
                for the selected month.
            </p>
            <input type="number"
                   id="modalIncomeInput"
                   class="modal-input"
                   placeholder="e.g., 50000">
            <div class="modal-btns">
                <button id="closeIncomeModalBtn" class="modal-cancel">
                    Cancel
                </button>
                <button id="saveIncomeModalBtn" class="modal-save">
                    💾 Save Income
                </button>
            </div>
        </div>
    </div>

    <script>
        window.currentUserName = "<%= userName %>";
    </script>
    <script src="script.js"></script>

</body>
</html>
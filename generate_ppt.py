import sys
import os
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN
from pptx.enum.shapes import MSO_SHAPE

def create_presentation():
    prs = Presentation()
    prs.slide_width = Inches(13.333)
    prs.slide_height = Inches(7.5)

    blank_slide_layout = prs.slide_layouts[6] # blank layout

    WHITE = RGBColor(255, 255, 255)
    BLACK = RGBColor(0, 0, 0)
    DARK_GRAY = RGBColor(60, 60, 60)

    def set_white_background(slide):
        bg = slide.background
        fill = bg.fill
        fill.solid()
        fill.fore_color.rgb = WHITE

    def add_header(slide, title_text, category_text=None):
        set_white_background(slide)
        
        # Category / Tracker text
        if category_text:
            cat_box = slide.shapes.add_textbox(Inches(0.8), Inches(0.4), Inches(11.5), Inches(0.35))
            tf_cat = cat_box.text_frame
            tf_cat.word_wrap = True
            p_cat = tf_cat.paragraphs[0]
            p_cat.text = category_text.upper()
            p_cat.font.size = Pt(10)
            p_cat.font.bold = True
            p_cat.font.name = "Arial"
            p_cat.font.color.rgb = DARK_GRAY

        # Title text
        top_pos = Inches(0.7) if category_text else Inches(0.5)
        title_box = slide.shapes.add_textbox(Inches(0.8), top_pos, Inches(11.7), Inches(0.75))
        tf = title_box.text_frame
        tf.word_wrap = True
        p = tf.paragraphs[0]
        p.text = title_text
        p.font.size = Pt(24)
        p.font.bold = True
        p.font.name = "Arial"
        p.font.color.rgb = BLACK

        # Divider line
        line = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0.8), Inches(1.5), Inches(11.733), Inches(0.02))
        line.fill.solid()
        line.fill.fore_color.rgb = BLACK
        line.line.color.rgb = BLACK

    # ==========================================
    # SLIDE 1: TITLE SLIDE
    # ==========================================
    slide1 = prs.slides.add_slide(blank_slide_layout)
    set_white_background(slide1)

    # Outer decorative frame
    border = slide1.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0.6), Inches(0.6), Inches(12.133), Inches(6.3))
    border.fill.background()
    border.line.color.rgb = BLACK
    border.line.width = Pt(1.5)

    # Inner container
    box1 = slide1.shapes.add_textbox(Inches(1.2), Inches(1.2), Inches(10.9), Inches(4.8))
    tf1 = box1.text_frame
    tf1.word_wrap = True

    p_super = tf1.paragraphs[0]
    p_super.text = "PROJECT REVIEW / DEFENSE PRESENTATION"
    p_super.font.size = Pt(12)
    p_super.font.bold = True
    p_super.font.name = "Arial"
    p_super.font.color.rgb = DARK_GRAY
    p_super.space_after = Pt(18)

    p_title = tf1.add_paragraph()
    p_title.text = "WasteWise AI: Smart Food Waste Prediction and Management System"
    p_title.font.size = Pt(30)
    p_title.font.bold = True
    p_title.font.name = "Arial"
    p_title.font.color.rgb = BLACK
    p_title.space_after = Pt(14)

    p_sub = tf1.add_paragraph()
    p_sub.text = "Predictive Food Demand Analytics, Waste Minimization & Preparation Optimization Using Machine Learning"
    p_sub.font.size = Pt(15)
    p_sub.font.name = "Arial"
    p_sub.font.color.rgb = DARK_GRAY
    p_sub.space_after = Pt(36)

    # Divider line inside title
    inner_line = slide1.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(1.2), Inches(4.2), Inches(10.9), Inches(0.02))
    inner_line.fill.solid()
    inner_line.fill.fore_color.rgb = BLACK
    inner_line.line.color.rgb = BLACK

    # Metadata Box
    meta_box = slide1.shapes.add_textbox(Inches(1.2), Inches(4.5), Inches(10.9), Inches(2.1))
    tf_meta = meta_box.text_frame
    tf_meta.word_wrap = True

    p_meta1 = tf_meta.paragraphs[0]
    p_meta1.text = "Presented by: [Project Team / Student Name(s)]  |  Roll No / Register No: [Your ID/Roll No]"
    p_meta1.font.size = Pt(13)
    p_meta1.font.bold = True
    p_meta1.font.name = "Arial"
    p_meta1.font.color.rgb = BLACK
    p_meta1.space_after = Pt(6)

    p_meta2 = tf_meta.add_paragraph()
    p_meta2.text = "Under the Guidance of: [Guide Name / Designation]  |  Department of Computer Science & Engineering"
    p_meta2.font.size = Pt(12)
    p_meta2.font.name = "Arial"
    p_meta2.font.color.rgb = DARK_GRAY
    p_meta2.space_after = Pt(6)

    p_meta3 = tf_meta.add_paragraph()
    p_meta3.text = "Institution: [College / University Name]  |  Academic Year: 2025 – 2026"
    p_meta3.font.size = Pt(12)
    p_meta3.font.italic = True
    p_meta3.font.name = "Arial"
    p_meta3.font.color.rgb = DARK_GRAY

    # ==========================================
    # SLIDE 2: INTRODUCTION & PROBLEM STATEMENT
    # ==========================================
    slide2 = prs.slides.add_slide(blank_slide_layout)
    add_header(slide2, "Introduction & Problem Statement", "Background & Motivation")

    # Left box: Introduction
    intro_box = slide2.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0.8), Inches(1.8), Inches(5.7), Inches(5.1))
    intro_box.fill.solid()
    intro_box.fill.fore_color.rgb = WHITE
    intro_box.line.color.rgb = BLACK
    intro_box.line.width = Pt(1)

    tb_intro = slide2.shapes.add_textbox(Inches(1.0), Inches(1.95), Inches(5.3), Inches(4.8))
    tf_intro = tb_intro.text_frame
    tf_intro.word_wrap = True

    p_in_t = tf_intro.paragraphs[0]
    p_in_t.text = "Introduction"
    p_in_t.font.size = Pt(18)
    p_in_t.font.bold = True
    p_in_t.font.name = "Arial"
    p_in_t.font.color.rgb = BLACK
    p_in_t.space_after = Pt(12)

    intro_points = [
        "Global Food Crisis: Over 1.3 billion tons of food is wasted annually worldwide (UN FAO).",
        "Canteen Surplus: Institutional cafeterias produce 20% to 35% surplus food waste daily.",
        "Environmental Impact: Organic landfill waste produces high methane emissions driving climate change.",
        "Economic Loss: Kitchen managers suffer continuous financial losses from over-preparation.",
        "AI Opportunity: Machine learning can accurately forecast meal demand prior to cooking."
    ]
    for pt in intro_points:
        p = tf_intro.add_paragraph()
        p.text = "• " + pt
        p.font.size = Pt(11.5)
        p.font.name = "Arial"
        p.font.color.rgb = DARK_GRAY
        p.space_after = Pt(9)

    # Right box: Problem Statement
    prob_box = slide2.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(6.833), Inches(1.8), Inches(5.7), Inches(5.1))
    prob_box.fill.solid()
    prob_box.fill.fore_color.rgb = WHITE
    prob_box.line.color.rgb = BLACK
    prob_box.line.width = Pt(1)

    tb_prob = slide2.shapes.add_textbox(Inches(7.033), Inches(1.95), Inches(5.3), Inches(4.8))
    tf_prob = tb_prob.text_frame
    tf_prob.word_wrap = True

    p_pr_t = tf_prob.paragraphs[0]
    p_pr_t.text = "Problem Statement"
    p_pr_t.font.size = Pt(18)
    p_pr_t.font.bold = True
    p_pr_t.font.name = "Arial"
    p_pr_t.font.color.rgb = BLACK
    p_pr_t.space_after = Pt(12)

    prob_points = [
        "Unpredictable Attendance: Customer headcount fluctuates with exams, weather, weekends & events.",
        "Manual Guesswork: Chefs rely on static rule-of-thumb ratios rather than predictive analytics.",
        "Retrospective Audits: Post-dining food weighing cannot prevent over-cooking before it happens.",
        "Isolated Variables: Existing solutions ignore combined contextual parameters (rain, occupancy, menus).",
        "Target Goal: An on-device mobile AI system to forecast prep quantities and alert waste risks proactively."
    ]
    for pt in prob_points:
        p = tf_prob.add_paragraph()
        p.text = "• " + pt
        p.font.size = Pt(11.5)
        p.font.name = "Arial"
        p.font.color.rgb = DARK_GRAY
        p.space_after = Pt(9)

    # ==========================================
    # SLIDE 3: OBJECTIVES
    # ==========================================
    slide3 = prs.slides.add_slide(blank_slide_layout)
    add_header(slide3, "Objectives", "Project Goals & Scope")

    objectives = [
        ("Demand & Waste Forecasting", 
         "Develop supervised regression models (Ridge, CART, Random Forest) to predict daily consumption & waste."),
        ("26-Factor Feature Engineering", 
         "Encode multi-dimensional variables (weather, exams, hostel occupancy, menu popularity, feedback)."),
        ("Pre-Cook Prep Guidance", 
         "Deliver actionable cooking quantities and categorize waste risks into LOW, MEDIUM, and HIGH alert levels."),
        ("Benchmarking & Auto Selection", 
         "Evaluate ML algorithms side-by-side (MAE, RMSE, R²) with dynamic on-device best model deployment."),
        ("Intuitive Offline Mobile App", 
         "Engineer an offline-first Jetpack Compose app with Room SQLite database and actionable pre-cook advisory engine."),
        ("Financial & Environmental Savings", 
         "Calculate real-time cost savings (₹/kg) and landfill waste cuts, advancing UN SDG 12 sustainability.")
    ]

    for idx, (title, desc) in enumerate(objectives):
        col = idx % 2
        row = idx // 2
        x = Inches(0.8 + col * 5.95)
        y = Inches(1.8 + row * 1.68)
        w = Inches(5.75)
        h = Inches(1.52)

        card = slide3.shapes.add_shape(MSO_SHAPE.RECTANGLE, x, y, w, h)
        card.fill.solid()
        card.fill.fore_color.rgb = WHITE
        card.line.color.rgb = BLACK
        card.line.width = Pt(1)

        tb = slide3.shapes.add_textbox(x + Inches(0.18), y + Inches(0.12), w - Inches(0.36), h - Inches(0.24))
        tf = tb.text_frame
        tf.word_wrap = True

        p1 = tf.paragraphs[0]
        p1.text = f"Obj {idx+1}: {title}"
        p1.font.size = Pt(13)
        p1.font.bold = True
        p1.font.name = "Arial"
        p1.font.color.rgb = BLACK
        p1.space_after = Pt(4)

        p2 = tf.add_paragraph()
        p2.text = desc
        p2.font.size = Pt(11)
        p2.font.name = "Arial"
        p2.font.color.rgb = DARK_GRAY

    # ==========================================
    # SLIDE 4: LITERATURE SURVEY
    # ==========================================
    slide4 = prs.slides.add_slide(blank_slide_layout)
    add_header(slide4, "Literature Survey", "Related Work & Gap Analysis")

    rows = 6
    cols = 4
    table_shape = slide4.shapes.add_table(rows, cols, Inches(0.8), Inches(1.8), Inches(11.733), Inches(5.0))
    table = table_shape.table

    table.columns[0].width = Inches(2.2)  # Author & Year
    table.columns[1].width = Inches(3.2)  # Methodology / Focus
    table.columns[2].width = Inches(3.4)  # Key Contributions
    table.columns[3].width = Inches(2.933) # Limitations & Gaps

    headers = ["Author & Year", "Methodology / Focus", "Key Findings", "Identified Research Gaps"]
    for j, h in enumerate(headers):
        cell = table.cell(0, j)
        cell.fill.solid()
        cell.fill.fore_color.rgb = WHITE
        tf = cell.text_frame
        tf.word_wrap = True
        p = tf.paragraphs[0]
        p.text = h
        p.font.size = Pt(12)
        p.font.bold = True
        p.font.name = "Arial"
        p.font.color.rgb = BLACK

    survey_data = [
        ("Eriksson et al. (2018)\n[Elsevier]",
         "Time-series & regression in educational catering.",
         "Identified student attendance & dish type as drivers of 23% waste.",
         "Lacks real-time multi-contextual inputs & mobile decision support."),
        
        ("Stockle et al. (2020)\n[IEEE Access]",
         "IoT smart bin sensor telemetry & cloud analytics.",
         "Automated waste measurement & tray image classification.",
         "High hardware cost; reactive audit only (measures waste post-disposal)."),
         
        ("Vidal et al. (2021)\n[J. Clean Prod.]",
         "ANN & SVR for mess meal demand prediction.",
         "Machine learning outperformed simple rolling averages.",
         "Server-dependent; no on-device offline edge ML execution for staff."),
         
        ("Gaur et al. (2022)\n[Springer]",
         "Supervised ML for food demand forecasting.",
         "Random Forest regression reduced over-stocking by ~18%.",
         "Limited feature space; lacks pre-cook risk alerts & actionable advice."),
         
        ("WasteWise AI\n(Proposed Work)",
         "On-Device Ensemble ML (RF, DT, Ridge) + Real-Time Waste Advisory.",
         "Holistic pre-cook forecasting, R² ~ 0.89, prep advice & risk rating.",
         "Directly solves prior gaps: offline edge ML, zero hardware cost, proactive.")
    ]

    for i, row in enumerate(survey_data):
        for j, val in enumerate(row):
            cell = table.cell(i+1, j)
            cell.fill.solid()
            cell.fill.fore_color.rgb = WHITE
            tf = cell.text_frame
            tf.word_wrap = True
            p = tf.paragraphs[0]
            p.text = val
            p.font.size = Pt(10 if i<4 else 10.5)
            p.font.name = "Arial"
            if i == 4:
                p.font.bold = True
                p.font.color.rgb = BLACK
            else:
                p.font.color.rgb = BLACK if j == 0 else DARK_GRAY

    # ==========================================
    # SLIDE 5: EXISTING VS. PROPOSED SYSTEM
    # ==========================================
    slide5 = prs.slides.add_slide(blank_slide_layout)
    add_header(slide5, "Existing vs. Proposed System", "Comparative Evaluation")

    rows = 8
    cols = 3
    table_shape5 = slide5.shapes.add_table(rows, cols, Inches(0.8), Inches(1.8), Inches(11.733), Inches(5.1))
    table5 = table_shape5.table

    table5.columns[0].width = Inches(2.733)
    table5.columns[1].width = Inches(4.5)
    table5.columns[2].width = Inches(4.5)

    headers5 = ["Comparison Dimension", "Conventional / Existing System", "Proposed WasteWise AI System"]
    for j, h in enumerate(headers5):
        cell = table5.cell(0, j)
        cell.fill.solid()
        cell.fill.fore_color.rgb = WHITE
        tf = cell.text_frame
        tf.word_wrap = True
        p = tf.paragraphs[0]
        p.text = h
        p.font.size = Pt(12)
        p.font.bold = True
        p.font.name = "Arial"
        p.font.color.rgb = BLACK

    comparisons = [
        ("Forecasting Approach",
         "Manual intuition, static rule-of-thumb, or simple rolling averages.",
         "Supervised ML suite (Ridge Regression, Decision Tree, Random Forest)."),
        
        ("Contextual Features",
         "Basic day-of-week or headcount guesswork only.",
         "26 encoded features (weather, exams, occupancy, menu feedback, etc.)."),

        ("Intervention Timing",
         "Reactive: Weighing discarded food after disposal.",
         "Proactive: Pre-cooking preparation guidance & risk mitigation."),

        ("Risk Assessment",
         "No risk rating; chefs unaware of upcoming surplus.",
         "Automated waste risk rating (LOW, MEDIUM, HIGH) with financial alerts."),

        ("Deployment & Cost",
         "Expensive smart bins or cumbersome spreadsheets.",
         "Native Android app running locally on existing devices (Zero Cost)."),

        ("Connectivity & Storage",
         "Cloud reliant; fails under poor internet connectivity.",
         "Offline-first Room database & in-app Kotlin ML engine."),

        ("Model Adaptability",
         "Fixed formulas that never improve over time.",
         "Continuous on-device evaluation & dynamic best-model selection (R² ~ 0.89).")
    ]

    for i, (dim, exist, prop) in enumerate(comparisons):
        c0 = table5.cell(i+1, 0)
        c0.fill.solid()
        c0.fill.fore_color.rgb = WHITE
        p0 = c0.text_frame.paragraphs[0]
        p0.text = dim
        p0.font.bold = True
        p0.font.size = Pt(11)
        p0.font.name = "Arial"
        p0.font.color.rgb = BLACK

        c1 = table5.cell(i+1, 1)
        c1.fill.solid()
        c1.fill.fore_color.rgb = WHITE
        p1 = c1.text_frame.paragraphs[0]
        p1.text = "• " + exist
        p1.font.size = Pt(10.5)
        p1.font.name = "Arial"
        p1.font.color.rgb = DARK_GRAY

        c2 = table5.cell(i+1, 2)
        c2.fill.solid()
        c2.fill.fore_color.rgb = WHITE
        p2 = c2.text_frame.paragraphs[0]
        p2.text = "• " + prop
        p2.font.size = Pt(10.5)
        p2.font.name = "Arial"
        p2.font.color.rgb = BLACK

    # ==========================================
    # SLIDE 6: SYSTEM REQUIREMENTS
    # ==========================================
    slide6 = prs.slides.add_slide(blank_slide_layout)
    add_header(slide6, "System Requirements", "Hardware & Software Specifications")

    # Left Box: Hardware Requirements
    card_hw = slide6.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0.8), Inches(1.8), Inches(5.7), Inches(5.1))
    card_hw.fill.solid()
    card_hw.fill.fore_color.rgb = WHITE
    card_hw.line.color.rgb = BLACK
    card_hw.line.width = Pt(1)

    tb_hw = slide6.shapes.add_textbox(Inches(1.0), Inches(1.95), Inches(5.3), Inches(4.8))
    tf_hw = tb_hw.text_frame
    tf_hw.word_wrap = True

    p_hw_t = tf_hw.paragraphs[0]
    p_hw_t.text = "Hardware Requirements"
    p_hw_t.font.size = Pt(18)
    p_hw_t.font.bold = True
    p_hw_t.font.name = "Arial"
    p_hw_t.font.color.rgb = BLACK
    p_hw_t.space_after = Pt(10)

    hw_specs = [
        ("Development Workstation:", "PC/Laptop with Intel i5 / Ryzen 5+ processor."),
        ("Development RAM:", "16 GB RAM recommended (min 8 GB) for Android Studio."),
        ("Storage:", "256 GB SSD (min 25 GB free disk space for Android SDK)."),
        ("Target Mobile Device:", "Any Android Smartphone/Tablet running Android 8.0+ (API 26+)."),
        ("Device Specifications:", "3 GB+ RAM, Quad-Core 1.8 GHz CPU, 100 MB free internal storage."),
        ("Connectivity:", "Standalone offline-first execution (Zero network dependency for ML inference).")
    ]
    for label, val in hw_specs:
        p = tf_hw.add_paragraph()
        p.text = f"• {label} {val}"
        p.font.size = Pt(11)
        p.font.name = "Arial"
        p.font.color.rgb = DARK_GRAY
        p.space_after = Pt(8)

    # Right Box: Software Requirements
    card_sw = slide6.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(6.833), Inches(1.8), Inches(5.7), Inches(5.1))
    card_sw.fill.solid()
    card_sw.fill.fore_color.rgb = WHITE
    card_sw.line.color.rgb = BLACK
    card_sw.line.width = Pt(1)

    tb_sw = slide6.shapes.add_textbox(Inches(7.033), Inches(1.95), Inches(5.3), Inches(4.8))
    tf_sw = tb_sw.text_frame
    tf_sw.word_wrap = True

    p_sw_t = tf_sw.paragraphs[0]
    p_sw_t.text = "Software Requirements"
    p_sw_t.font.size = Pt(18)
    p_sw_t.font.bold = True
    p_sw_t.font.name = "Arial"
    p_sw_t.font.color.rgb = BLACK
    p_sw_t.space_after = Pt(10)

    sw_specs = [
        ("Operating System:", "Windows 10/11 (64-bit), Ubuntu Linux, or macOS."),
        ("Development IDE:", "Android Studio Ladybug / Hedgehog with Android SDK 34."),
        ("Programming Language:", "Kotlin (v1.9+ / 2.0+) using Coroutines & Flow paradigms."),
        ("UI Toolkit:", "Jetpack Compose with Material Design 3 (M3)."),
        ("Local Persistence:", "Android Room Database (SQLite wrapper)."),
        ("ML Engine:", "Custom Kotlin In-Memory Regression Suite (Ridge, CART, Random Forest)."),
        ("Advisory Engine:", "Rule-Based & ML Recommendation System for prep deltas & financial risk.")
    ]
    for label, val in sw_specs:
        p = tf_sw.add_paragraph()
        p.text = f"• {label} {val}"
        p.font.size = Pt(11)
        p.font.name = "Arial"
        p.font.color.rgb = DARK_GRAY
        p.space_after = Pt(8)

    # ==========================================
    # SLIDE 7: METHODOLOGY & ARCHITECTURE
    # ==========================================
    slide7 = prs.slides.add_slide(blank_slide_layout)
    add_header(slide7, "Proposed Methodology & Architecture", "System Design & Operational Pipeline")

    # Architecture Container Box
    diag_box = slide7.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0.8), Inches(1.8), Inches(11.733), Inches(3.4))
    diag_box.fill.solid()
    diag_box.fill.fore_color.rgb = WHITE
    diag_box.line.color.rgb = BLACK
    diag_box.line.width = Pt(1.5)

    tb_dp = slide7.shapes.add_textbox(Inches(1.0), Inches(1.88), Inches(11.3), Inches(0.35))
    p_dp = tb_dp.text_frame.paragraphs[0]
    p_dp.text = "SYSTEM ARCHITECTURE & DATA PIPELINE FLOW"
    p_dp.font.size = Pt(12)
    p_dp.font.bold = True
    p_dp.alignment = PP_ALIGN.CENTER
    p_dp.font.name = "Arial"
    p_dp.font.color.rgb = BLACK

    arch_blocks = [
        ("1. Presentation Layer\n(Jetpack Compose UI)", 
         "• Analytics Dashboard\n• 26-Factor Log Form\n• Historical Logs Table\n• Model Benchmark View\n• AI Prediction Screen"),
        ("2. Machine Learning Engine\n(In-Memory Regression)", 
         "• 26-Feature Pipeline\n• 80/20 Train-Test Split\n• Ridge Regression\n• CART Decision Tree\n• Random Forest Regressor"),
        ("3. Model Evaluation Unit\n(Decision Support)", 
         "• MAE, RMSE & R² Scoring\n• Auto Best Model Selector\n• Waste Risk Rating (L/M/H)\n• Financial Loss Calc\n• Recommended Prep (Kg)"),
        ("4. Data & Persistence Layer\n(Room DB & Storage)", 
         "• Room DB (SQLite)\n• 15k+ Dataset Generator\n• DAO Repository Layer\n• Coroutines Flow\n• Offline Persistence")
    ]

    for b_idx, (b_title, b_content) in enumerate(arch_blocks):
        bx = Inches(1.05 + b_idx * 2.8)
        by = Inches(2.3)
        bw = Inches(2.65)
        bh = Inches(2.7)

        block_shape = slide7.shapes.add_shape(MSO_SHAPE.RECTANGLE, bx, by, bw, bh)
        block_shape.fill.solid()
        block_shape.fill.fore_color.rgb = WHITE
        block_shape.line.color.rgb = BLACK
        block_shape.line.width = Pt(1)

        tb_b = slide7.shapes.add_textbox(bx + Inches(0.08), by + Inches(0.08), bw - Inches(0.16), bh - Inches(0.16))
        tf_b = tb_b.text_frame
        tf_b.word_wrap = True

        p_bt = tf_b.paragraphs[0]
        p_bt.text = b_title
        p_bt.font.size = Pt(11)
        p_bt.font.bold = True
        p_bt.alignment = PP_ALIGN.CENTER
        p_bt.font.name = "Arial"
        p_bt.font.color.rgb = BLACK
        p_bt.space_after = Pt(6)

        p_bc = tf_b.add_paragraph()
        p_bc.text = b_content
        p_bc.font.size = Pt(9.5)
        p_bc.font.name = "Arial"
        p_bc.font.color.rgb = DARK_GRAY

    # Workflow Steps below diagram
    flow_box = slide7.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0.8), Inches(5.35), Inches(11.733), Inches(1.75))
    flow_box.fill.solid()
    flow_box.fill.fore_color.rgb = WHITE
    flow_box.line.color.rgb = BLACK
    flow_box.line.width = Pt(1)

    tb_fl = slide7.shapes.add_textbox(Inches(0.95), Inches(5.42), Inches(11.4), Inches(1.6))
    tf_fl = tb_fl.text_frame
    tf_fl.word_wrap = True

    p_fl_t = tf_fl.paragraphs[0]
    p_fl_t.text = "Operational Methodology Workflow:"
    p_fl_t.font.size = Pt(12)
    p_fl_t.font.bold = True
    p_fl_t.font.name = "Arial"
    p_fl_t.font.color.rgb = BLACK
    p_fl_t.space_after = Pt(4)

    steps = [
        "Step 1: Input meal parameters & environmental context (Weather, Exams, Occupancy, Menu).",
        "Step 2: FeaturePipeline encodes categorical values & normalizes feature vectors.",
        "Step 3: ML Engine trains Ridge, CART & Random Forest; automatically selects top model (RF R² ~ 0.89).",
        "Step 4: System displays exact prep recommendation (kg), waste risk alert & financial savings insights."
    ]
    for s in steps:
        p = tf_fl.add_paragraph()
        p.text = "• " + s
        p.font.size = Pt(10.5)
        p.font.name = "Arial"
        p.font.color.rgb = DARK_GRAY

    # ==========================================
    # SLIDE 8: WORK COMPLETED SO FAR (100% COMPLETED)
    # ==========================================
    slide8 = prs.slides.add_slide(blank_slide_layout)
    add_header(slide8, "Work Completed So Far", "Project Implementation Status (100% Completed)")

    milestones = [
        ("Dataset & Feature Engineering", 
         "100% Completed: Synthesized 26 contextual variables & 15,000+ realistic mess records with ordinal encoding."),
        ("Pure Kotlin Edge ML Engine", 
         "100% Completed: Implemented Ridge Linear Regression, CART Decision Trees, & Random Forest Regressor on-device."),
        ("Automated Model Evaluation", 
         "100% Completed: Evaluates MAE, RMSE & R² automatically; Random Forest deployed as best model (R² ~ 0.89)."),
        ("Room Database Architecture", 
         "100% Completed: Built SQLite Room entities, DAOs, Flow repository layer, and offline persistence workflow."),
        ("Complete Jetpack Compose Mobile App", 
         "100% Completed: Developed 7 production-ready UI screens: Dashboard, Add Data, History, Benchmark & AI Insights."),
        ("Actionable Prep & Waste Risk Advisory Engine", 
         "100% Completed: Built dynamic cooking batch recommendations, LOW/MED/HIGH waste risk classification, and financial loss/savings computation.")
    ]

    for idx, (title, desc) in enumerate(milestones):
        col = idx % 2
        row = idx // 2
        x = Inches(0.8 + col * 5.95)
        y = Inches(1.8 + row * 1.68)
        w = Inches(5.75)
        h = Inches(1.52)

        card = slide8.shapes.add_shape(MSO_SHAPE.RECTANGLE, x, y, w, h)
        card.fill.solid()
        card.fill.fore_color.rgb = WHITE
        card.line.color.rgb = BLACK
        card.line.width = Pt(1)

        tb = slide8.shapes.add_textbox(x + Inches(0.18), y + Inches(0.12), w - Inches(0.36), h - Inches(0.24))
        tf = tb.text_frame
        tf.word_wrap = True

        p1 = tf.paragraphs[0]
        p1.text = f"✓ Milestone {idx+1}: {title}"
        p1.font.size = Pt(13)
        p1.font.bold = True
        p1.font.name = "Arial"
        p1.font.color.rgb = BLACK
        p1.space_after = Pt(4)

        p2 = tf.add_paragraph()
        p2.text = desc
        p2.font.size = Pt(11)
        p2.font.name = "Arial"
        p2.font.color.rgb = DARK_GRAY

    # ==========================================
    # SLIDE 9: EXPECTED OUTCOMES
    # ==========================================
    slide9 = prs.slides.add_slide(blank_slide_layout)
    add_header(slide9, "Expected Outcomes", "Target Impact & Quantified Benefits")

    outcomes = [
        ("25% – 35% Food Waste Reduction", 
         "Precision ML demand forecasting prevents daily food surplus before cooking commences."),
        ("High Predictive Accuracy (R² ~ 0.89)", 
         "Ensemble Random Forest regressor achieves high R² > 0.88 with minimal MAE/RMSE on test records."),
        ("15% – 25% Procurement Cost Savings", 
         "Substantially cuts raw material expenditure by curbing over-preparation of high-cost food items."),
        ("Zero Hardware Cost Edge Deployment", 
         "Runs locally on staff Android smartphones/tablets without requiring expensive IoT weighing bins."),
        ("Real-Time Proactive Waste Risk Alerts", 
         "Instant LOW / MEDIUM / HIGH risk ratings with estimated financial loss warnings empower kitchen managers."),
        ("Reduced Carbon Footprint (UN SDG 12)", 
         "Curtails landfill organic waste decomposition, directly reducing methane and greenhouse gas emissions.")
    ]

    for idx, (title, desc) in enumerate(outcomes):
        col = idx % 2
        row = idx // 2
        x = Inches(0.8 + col * 5.95)
        y = Inches(1.8 + row * 1.68)
        w = Inches(5.75)
        h = Inches(1.52)

        card = slide9.shapes.add_shape(MSO_SHAPE.RECTANGLE, x, y, w, h)
        card.fill.solid()
        card.fill.fore_color.rgb = WHITE
        card.line.color.rgb = BLACK
        card.line.width = Pt(1)

        tb = slide9.shapes.add_textbox(x + Inches(0.18), y + Inches(0.12), w - Inches(0.36), h - Inches(0.24))
        tf = tb.text_frame
        tf.word_wrap = True

        p1 = tf.paragraphs[0]
        p1.text = f"★ Outcome {idx+1}: {title}"
        p1.font.size = Pt(13)
        p1.font.bold = True
        p1.font.name = "Arial"
        p1.font.color.rgb = BLACK
        p1.space_after = Pt(4)

        p2 = tf.add_paragraph()
        p2.text = desc
        p2.font.size = Pt(11)
        p2.font.name = "Arial"
        p2.font.color.rgb = DARK_GRAY

    # ==========================================
    # SLIDE 10: REFERENCES
    # ==========================================
    slide10 = prs.slides.add_slide(blank_slide_layout)
    add_header(slide10, "References", "Academic & Technical Literature")

    ref_box = slide10.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0.8), Inches(1.8), Inches(11.733), Inches(5.1))
    ref_box.fill.solid()
    ref_box.fill.fore_color.rgb = WHITE
    ref_box.line.color.rgb = BLACK
    ref_box.line.width = Pt(1)

    tb_ref = slide10.shapes.add_textbox(Inches(1.0), Inches(1.95), Inches(11.3), Inches(4.8))
    tf_ref = tb_ref.text_frame
    tf_ref.word_wrap = True

    references = [
        "[1] UN FAO, \"Food Wastage Footprint: Impacts on Natural Resources,\" Technical Report, Rome, 2021.",
        "[2] M. Eriksson et al., \"Food waste in educational catering: Quantities & prevention,\" Res. Conserv. Recycl., vol. 138, pp. 245–253, 2018.",
        "[3] M. A. Stockle et al., \"Smart Waste Audit System: IoT-Driven Waste Tracking,\" IEEE Access, vol. 8, pp. 112450–112462, 2020.",
        "[4] R. Vidal et al., \"Predictive ML Modeling for Demand Forecasting in Dining Centers,\" J. Clean. Prod., vol. 294, p. 126201, 2021.",
        "[5] P. Gaur et al., \"Comparative Evaluation of ML Algorithms for Food Demand Forecasting,\" SN Appl. Sci., vol. 4, no. 6, pp. 1–14, 2022.",
        "[6] L. Breiman, \"Random Forests,\" Machine Learning, vol. 45, no. 1, pp. 5–32, 2001.",
        "[7] Google Developers, \"Jetpack Compose & Android Architecture Components Documentation,\" Google LLC, 2024.",
        "[8] Google Developers, \"Android Room Persistence Library Guide,\" Google LLC, 2024. [Online]. Available: https://developer.android.com/training/data-storage/room"
    ]

    for idx, ref in enumerate(references):
        p = tf_ref.paragraphs[0] if idx == 0 else tf_ref.add_paragraph()
        p.text = ref
        p.font.size = Pt(11)
        p.font.name = "Arial"
        p.font.color.rgb = DARK_GRAY
        p.space_after = Pt(8)

    output_path = os.path.join(os.getcwd(), "WasteWise_AI_Presentation.pptx")
    prs.save(output_path)
    print(f"Presentation saved successfully to: {output_path}")

if __name__ == "__main__":
    create_presentation()

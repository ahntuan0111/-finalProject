"""
audit_fix_numbering.py
─────────────────────────────────────────────────────────────────────────────
Phase 1: Scans V20.docx and prints ALL Figure/Table captions found in Chapter 3.
Run this first to audit, then set FIX_MODE = True to apply changes.
─────────────────────────────────────────────────────────────────────────────
"""
import os, sys, re

try:
    from docx import Document
    from docx.shared import Pt
    from docx.enum.text import WD_PARAGRAPH_ALIGNMENT, WD_LINE_SPACING
except ImportError:
    import subprocess
    subprocess.check_call([sys.executable, "-m", "pip", "install", "python-docx"])
    from docx import Document
    from docx.shared import Pt
    from docx.enum.text import WD_PARAGRAPH_ALIGNMENT, WD_LINE_SPACING

def main():
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')

    doc_path = r"e:\Final_Project\docx\MaSci_Final_Report_V20.docx"
    doc = Document(doc_path)
    paras = doc.paragraphs

    # Find Chapter 3 boundaries
    chap3_start = chap4_start = -1
    for i, p in enumerate(paras):
        t = p.text.strip().upper()
        if chap3_start == -1 and "CHAPTER 3" in t:
            chap3_start = i
        elif chap3_start != -1 and chap4_start == -1 and "CHAPTER 4" in t:
            chap4_start = i
            break

    if chap3_start == -1:
        print("[ERROR] Chapter 3 not found.")
        return
    if chap4_start == -1:
        chap4_start = len(paras)

    print("=" * 70)
    print(f"Chapter 3: paragraphs {chap3_start} to {chap4_start - 1}")
    print("=" * 70)
    print()

    # Pattern to detect captions
    fig_pat = re.compile(r'Figure\s+3\.(\d+)', re.IGNORECASE)
    tbl_pat = re.compile(r'Table\s+3\.(\d+)', re.IGNORECASE)

    print("--- FIGURE CAPTIONS FOUND ---")
    for i in range(chap3_start, chap4_start):
        p = paras[i]
        t = p.text.strip()
        # Caption: italic or centered, starts with Figure
        if t.startswith("Figure") or (fig_pat.search(t) and len(t) < 200):
            if t.startswith("Figure"):
                print(f"  Para {i:4d}: {t[:100]}")

    print()
    print("--- TABLE CAPTIONS FOUND ---")
    for i in range(chap3_start, chap4_start):
        p = paras[i]
        t = p.text.strip()
        if t.startswith("Table"):
            print(f"  Para {i:4d}: {t[:100]}")

    print()
    print("--- ALL FIGURE/TABLE REFERENCES IN BODY TEXT ---")
    for i in range(chap3_start, chap4_start):
        p = paras[i]
        t = p.text.strip()
        if (fig_pat.search(t) or tbl_pat.search(t)) and not t.startswith("Figure") and not t.startswith("Table"):
            print(f"  Para {i:4d}: {t[:120]}")

if __name__ == "__main__":
    main()

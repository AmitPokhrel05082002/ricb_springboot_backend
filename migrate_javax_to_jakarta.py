#!/usr/bin/env python3
import os
import re
from pathlib import Path

# Directory to search
src_dir = Path("C:\\Users\\lekih\\RICB_backend\\src")

# Mapping of javax to jakarta packages
replacements = [
    (r'import javax\.persistence\.', 'import jakarta.persistence.'),
    (r'import javax\.transaction\.', 'import jakarta.transaction.'),
    (r'import javax\.validation\.', 'import jakarta.validation.'),
    (r'import javax\.mail\.', 'import jakarta.mail.'),
    (r'import javax\.activation\.', 'import jakarta.activation.'),
    # Keep javax.xml as is
]

# Find all Java files
java_files = list(src_dir.glob('**/*.java'))
print(f"Found {len(java_files)} Java files")

for java_file in java_files:
    try:
        with open(java_file, 'r', encoding='utf-8') as f:
            content = f.read()

        original_content = content

        # Apply replacements
        for pattern, replacement in replacements:
            content = re.sub(pattern, replacement, content)

        # Write back only if changed
        if content != original_content:
            with open(java_file, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Updated: {java_file.relative_to(src_dir)}")
    except Exception as e:
        print(f"Error processing {java_file}: {e}")

print("Migration complete!")


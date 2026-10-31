"""Summarize explicit live-test usage records without copying HTTP logs."""

import json
import math
import os
from pathlib import Path
import re
import sys
import xml.etree.ElementTree as ET


def summarize(*results):
    """Return measured counters from JUnit directories, excluding other log content."""
    metrics = {
        "requests", "usage_received", "input_tokens", "output_tokens", "total_tokens",
        "cached_tokens", "cache_write_tokens", "reasoning_tokens",
        "input_text_tokens", "input_image_tokens", "output_text_tokens", "output_image_tokens",
        "audio_seconds", "characters",
    }
    totals = {}
    invalid = 0
    reports = sorted({report for directory in results for report in directory.glob("TEST-*.xml")})
    for report in reports:
        try:
            suite = ET.parse(report)
        except ET.ParseError:
            invalid += 1
            continue
        for output in suite.iter("system-out"):
            for line in (output.text or "").splitlines():
                if not line.startswith("OPENAI_LIVE_USAGE "):
                    continue
                try:
                    record = json.loads(line.removeprefix("OPENAI_LIVE_USAGE "))
                    labels = record["model"], record["operation"]
                    if not all(isinstance(label, str) and re.fullmatch(r"[A-Za-z0-9._:/-]+", label) for label in labels):
                        raise ValueError("Invalid label")
                    values = {name: value for name, value in record.items() if name in metrics}
                    if not values or any(type(value) not in (int, float) or not math.isfinite(value) or value < 0 for value in values.values()):
                        raise ValueError("Invalid counter")
                except (ValueError, TypeError, KeyError):
                    invalid += 1
                    continue
                for name, value in values.items():
                    key = (*labels, name)
                    totals[key] = totals.get(key, 0) + value

    lines = [
        "## Live API usage",
        "",
        "Partial usage counters, not a bill or a whole-suite cost estimate. Token counts come from decoded responses; "
        "audio seconds and speech characters describe submitted fixtures. Failed, cancelled, missing-usage, and "
        "background work can incur charges that are absent here.",
        "",
    ]
    if totals:
        lines += ["| Model | Operation | Metric | Recorded total |", "| --- | --- | --- | ---: |"]
        lines += [f"| {model} | {operation} | {metric} | {value:g} |" for (model, operation, metric), value in sorted(totals.items())]
        lines += ["", "`usage_received` counts responses with token usage; `requests` counts recorded calls, not all suite requests."]
    else:
        lines.append("No usage records were produced. Tests may have been skipped, failed before recording usage, or not completed.")
    if invalid:
        lines += ["", f"Ignored {invalid} malformed usage records or unreadable test reports."]
    return "\n".join(lines) + "\n"


if __name__ == "__main__":
    results = [Path(path) for path in sys.argv[1:]] or [Path("openai-client/build/test-results/jvmTest")]
    summary = summarize(*results)
    if destination := os.environ.get("GITHUB_STEP_SUMMARY"):
        with open(destination, "a", encoding="utf-8") as output:
            output.write(summary)
    else:
        print(summary, end="")

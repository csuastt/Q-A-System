import "bytemd/dist/index.min.css";
import "github-markdown-css/github-markdown.css";
import breaks from "@bytemd/plugin-breaks";
import footnotes from "@bytemd/plugin-footnotes";
import gemoji from "@bytemd/plugin-gemoji";
import gfm from "@bytemd/plugin-gfm";
import highlight from "@bytemd/plugin-highlight";
import math from "@bytemd/plugin-math";
import mermaid from "@bytemd/plugin-mermaid";
import * as gfm_locales from "@bytemd/plugin-gfm/lib/locales/zh_Hans.json";
import * as math_locales from "@bytemd/plugin-math/lib/locales/zh_Hans.json";
import * as mermaid_locales from "@bytemd/plugin-mermaid/lib/locales/zh_Hans.json";
import * as editor_locales from "bytemd/lib/locales/zh_Hans.json";
import { Editor, Viewer } from "@bytemd/react";
import React from "react";

const plugins = [
    breaks(),
    footnotes(),
    gemoji(),
    gfm({ locale: gfm_locales }),
    highlight(),
    math({ locale: math_locales }),
    mermaid({ locale: mermaid_locales }),
];

interface MarkdownProps {
    value: string;
    onChange?: (newValue: string) => void;
    editorMode?: "split" | "tab" | "auto";
    viewOnly?: boolean;
}

const Markdown: React.FC<MarkdownProps> = (props) => {
    return props.viewOnly ? (
        <Viewer value={props.value} plugins={plugins} />
    ) : (
        <Editor
            value={props.value}
            onChange={props.onChange}
            plugins={plugins}
            mode={props.editorMode}
            locale={editor_locales}
        />
    );
};

export default Markdown;
